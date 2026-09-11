# crimson-citadel

> 덱빌딩 로그라이크 게임의 백엔드 서버. **Spring Boot 기초 과제**로 만들었다.

![저장된 여정 화면](docs/images/lv11.png)

10층의 탑을 오르며 카드를 모으고, 최종 보스를 잡는 싱글플레이 게임입니다.
프론트엔드(완성본)는 과제에 포함되어 있고, **서버 API를 직접 구현**하는 것이 과제의 내용이었습니다.
서버를 띄우면 `http://localhost:8080` 에서 실제로 플레이할 수 있습니다.

- 게임 생성 · 진행 저장 · 이어하기 · 이름 변경 · 삭제 (MySQL + JPA)
- 저장된 덱의 카드 수 집계 (N+1 없이)
- 외부 랭킹 API를 가공한 시즌 랭킹

## 실행

필요한 것: **JDK 21**, **Docker**

```bash
docker compose -f docker/compose.yml --env-file docker/.env up -d   # MySQL
./gradlew bootRun                                                   # 서버
```

| 항목 | 값 |
| --- | --- |
| 런타임 DB | MySQL 8.4 (Docker) |
| 호스트 포트 | **3308** → 컨테이너 3306 |
| 스키마 | `crimson_citadel` |
| 접속 정보 | `docker/.env` |

> 3306·3307을 피해 3308을 쓴다. 이유는 `docker/compose.yml` 주석에 적어 뒀습니다./\
> 해당 포트 설정 정보는 테스트 환경에 맞춰 환경설정 파일을 수정하셔야합니다./\
> 위치는 /src/main/resources 입니다.
## API

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| `POST` | `/games` | 게임 생성 |
| `GET` | `/games` | 목록 조회 — 카드 수 집계 포함 |
| `GET` | `/games/{gameId}` | 상세 조회 — 저장된 덱까지 |
| `PUT` | `/games/{gameId}/progress` | 진행 저장 |
| `PATCH` | `/games/{gameId}` | 플레이어 이름 변경 |
| `DELETE` | `/games/{gameId}` | 삭제 |
| `GET` | `/rankings` | 시즌 클리어 랭킹 — 외부 API 가공 |

## 구조

```
com.gamebasic
├── game/       controller · service · repository · entity · dto
├── runcard/    repository · entity · dto
├── ranking/    client · service · controller · dto   ← 외부 API를 읽는다. DB를 쓰지 않는다
└── common/     전역 예외 처리 · 공통 응답
```

Controller · Service · Repository를 분리한 3 Layer 구조입니다.
랭킹만 Repository 대신 `RankingClient` 를 두었습니다
— **외부 시스템에 닿는 계층**이라는 역할은 같습니다. \
대상이 DB에서 HTTP로 바뀐 것뿐이며, 이후 링크가 종료될 경우 사용할 수 있는 ranking.json 파일이 있습니다.
위치는 /src/main/resources/ranking/ranking.json 입니다.

---

## 과제 진행 순서

뼈대 코드가 주어지고, 실행하면 나는 에러를 하나씩 없애며 기능을 완성하는 방식이다.

| Lv | 주제 | 내용 |
| --- | --- | --- |
| 1 | 설정 파일 | Docker MySQL 연결, `ddl-auto` 선택 |
| 2 | 빈 등록 | 의존성 주입이 안 되는 원인 찾기 |
| 3 | RESTful 경로 | 명세와 경로·메서드 대조 |
| 4 | 트랜잭션 | `readOnly` 트랜잭션에서 쓰기가 막히는 버그 |
| 5 | 요청 검증 · 응답 DTO | Bean Validation, 엔티티를 직접 반환하지 않기 |
| 6 | 보상 카드 · 진행 저장 | 게임 루프 완성 |
| 7 | 목록 · 상세 조회 | 정렬 규칙, JPA Auditing |
| 8 | 수정 · 삭제 | 더티 체킹, 자식부터 삭제 |
| 9 | 상태 코드 | 끝난 게임 덮어쓰기 차단 (409) |
| 10 | 전역 예외 처리 | 404 · 409 응답 형식 통일 |
| 11 | N+1 없는 집계 | 목록 조회 쿼리 수를 게임 수와 무관하게 |
| 12 | 랭킹 | 외부 API 응답을 규칙대로 걸러 순위 산출 |

## 주요 쟁점

이 과제를 진행하며 답할 수 있는 주요 학습 내용입니다.

**단방향 연관관계만 사용하기**
`cascade` · `orphanRemoval` · 양방향 컬렉션이 금지되어 있었고
그래서 게임을 지울 때 자식(`RunCard`)을 **먼저, 명시적으로** 지워야 했습니다.
편의 기능을 걷어내면 "무엇이 언제 지워지는지"를 알게되었고 원본과 연관관계에 따라 삭제 되는 순서가 코드에서 부모 클래스와
자식클래스의 삭제 순서와 어떻게 다른지 알게 되었습니다.

**`save()` 없이 UPDATE 되는 이유**
이름 변경은 엔티티의 메서드만 호출하고 끝납니다. 영속성 컨텍스트가 변경을 감지해
트랜잭션 커밋 시점에 UPDATE를 처리합니다.

**404 · 409 · 400을 구분한다**
없는 게임은 404, 이미 끝난 게임에 진행을 저장하려 하면 409, 요청 형식이 틀리면 400을 반환합니다..
전부 `@RestControllerAdvice` 한 곳에서 같은 형식(`status` · `error` · `message` · `path`)으로 처리합니다.

**목록 조회의 쿼리 수를 고정한다**
기존 코드에서는 게임마다 카드 수를 세면 게임이 N개일 때 쿼리가 N+1번 실행됩니다.
JPQL `select new` DTO 프로젝션으로 `group by` 결과를 한 번에 받아 처리하는 기능을 구현하는 것으로
**게임 조회 1회 + 카드 수 집계 1회, 총 2회**로 줄일 수 있게됩니다.
해당 과정에서 어떻게 SQL 쿼리를 @Query 안에 넣기 위해 변환할지 연구했습니다.

**외부 데이터 검증 하기**
랭킹은 우리 DB가 아니라 외부 API가 주는 시즌 기록으로 만들었습니다.
그 응답에는 버그성 플레이와 형식이 어긋난 기록이 섞여있었습니다.
`deck.size` 처럼 클라이언트가 적어 보낼 숫자 정보는 직접 카운팅을 하고 비교하면서 검증합니다.
순위는 `클리어 시간(오름차순) → 남은 HP(내림차순) → 기록 id(오름차순)` 3단으로 정렬한 뒤 플레이어당 하나만 남도록 중복을 제거했습니다.
해당 과정에서는 C++의 함수 비교 객체처럼 Java의 Comparorator을 응용한 람다 함수 그리고 Stream을 결합하는 방식을 공부해 적용했습니다.

---

## 부록 · 랭킹 데이터 원본

`GET /rankings` 는 아래 외부 주소에서 시즌 기록 전체를 받아 가공했습니다.

```
https://f-api.github.io/game-spring-api-docs/basic/rankings.json
```

이 주소는 과제 제공처의 것이라 언젠가 링크가 종료될 수 있기에 그때도 이 저장소가 그대로 동작하도록,
받아온 응답 원본을 저장소에 함께 넣어 두었습니다.

| 항목 | 값 |
| --- | --- |
| 경로 | `src/main/resources/ranking/rankings.json` |
| 크기 | 2,838,880 bytes |
| SHA-256 | `d42084f640cdaaca486fcc6e2da3fb6c0aad1846050706db1910504b22bae35f` |
| 받은 날짜 | 2026-09-11 |
| 시즌 · 기록 수 | `2026-09` · 610건 |

`.gitattributes` 에서 줄끝 변환을 꺼 두었으므로 어느 OS에서 clone해도 위 체크섬이 그대로 나옵니다.

```bash
sha256sum src/main/resources/ranking/rankings.json
```

### 번들 파일로 전환하는 절차(ClaudeCode 제공)

외부 주소가 닫혔다면 아래 세 단계로 교체한다.

**1. 번들 파일을 로컬 HTTP로 띄웁니다.**

```bash
npx serve src/main/resources/ranking -l 9000
# 또는
python -m http.server 9000 --directory src/main/resources/ranking
```

확인:

```bash
curl -s http://localhost:9000/rankings.json | head -c 80
```

**2. `RankingClient` 의 주소 입력 값을 변경합니다.**

`src/main/java/com/gamebasic/ranking/client/RankingClient.java`

```java
private static final String SOURCE_URL = "http://localhost:9000/rankings.json";
```

**3. 서버를 재기동하고 확인합니다.**

```bash
curl -s http://localhost:8080/rankings
```

`"totalRecords":610` 과 `"season":"2026-09"` 가 나오면 전환이 끝난 것이다.
같은 데이터를 같은 방식(HTTP)으로 읽으므로 **랭킹 결과는 외부 API를 쓸 때와 완전히 같다.**

> 주소를 코드에 두지 않으려면 `application.properties` 에 `ranking.source-url` 을 만들고
> `RankingClient` 가 `@Value` 로 읽게 하면 된다. 이후로는 프로퍼티 한 줄만 고치면 됩니다.
> 
## 후기
(편집중)