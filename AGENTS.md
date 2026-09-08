# AGENTS.md — crimson-citadel 에이전트 공통 규약

> **이 파일이 단일 진실 공급원(SSOT)입니다.**
> Claude Code(`CLAUDE.md`)와 Antigravity(`.antigravity/rules/`)는 이 파일을 가리키는 얇은 포인터일 뿐입니다.
> 규칙을 바꿀 때는 **여기만** 고칩니다.

## 0. 세션 시작 시 반드시 할 것

1. `.agents/handoff/CURRENT.md` 를 읽는다. ← 직전 에이전트가 어디까지 했는지가 여기 있다.
2. `git status` / `git log --oneline -5` 로 실제 코드 상태와 대조한다.
3. 작업을 끝내면 `CURRENT.md` 를 갱신하고 `.agents/handoff/log/` 에 세션 기록을 남긴다.

CURRENT.md와 코드가 어긋나면 **코드가 정답**이다. CURRENT.md를 고쳐라.

## 1. 프로젝트

| 항목 | 값 |
| --- | --- |
| 이름 | `crimson-citadel` (Spring 기초 과제) |
| 런타임 | Java 21 (toolchain), Spring Boot 4.1.0 |
| DB | **MySQL (Docker)** — 런타임 유일. H2는 `testRuntimeOnly` 전용 |
| 빌드 | Gradle Wrapper (`./gradlew`) |
| 과제 명세 | `ASSIGNMENT.md` (Lv 1~12) |
| API 명세 | https://f-api.github.io/game-spring-api-docs/basic/api-docs.html |

## 2. 작업 모드 (COACH_MODE)

    COACH_MODE = on

- **on** — 이 저장소는 **학습 과제**다. 에이전트는 `TODO`의 정답 코드를 통째로 써 주지 않는다.
  대신 ① 실패 로그·명세를 근거로 원인을 짚고 ② 접근 방향과 검증 방법을 제시하고
  ③ 사용자가 쓴 코드를 리뷰한다. 사용자가 "직접 구현해줘"라고 명시하면 그 요청 범위에서만 구현한다.
- **off** — 일반 구현 모드. 위 제약 없이 구현한다.

> 모드를 바꾸려면 이 줄 하나만 `off` 로 고친다. 양쪽 툴에 동시에 반영된다.

## 3. 절대 규칙 (Hard Constraints)

과제 명세에서 온 제약이다. **위반하면 과제 자체가 오답이 된다.**

- 3 Layer Architecture 분리 — Controller / Service / Repository.
- 엔티티 연관관계는 **단방향만**. `cascade`, `orphanRemoval`, 양방향 컬렉션 **금지**.
  자식(`RunCard`)의 조회·저장·삭제는 Repository로 **명시적으로** 처리한다.
- 뼈대에 이미 있는 **클래스 이름과 패키지는 바꾸지 않는다**.
- API 경로 / JSON 필드명 / enum 값은 API 명세와 **한 글자도 다르면 안 된다**.
- 런타임 DB는 MySQL. `application.properties`에 H2를 쓰지 않는다.
- Lv 11: 목록 조회는 **쿼리 2회**(게임 조회 1 + 카드 수 집계 1). N+1 금지.
  카드 수 집계는 **JPQL `select new` DTO 프로젝션**. 인터페이스 프로젝션 금지.
- `./gradlew test` 는 **언제나 성공**해야 한다. 커밋 전 필수 확인.

## 4. 코드 컨벤션

- 주석·커밋 메시지·문서는 **한국어**. 식별자는 영어.
- 주변 코드의 주석 밀도와 네이밍을 그대로 따른다. 뼈대 스타일을 바꾸지 않는다.
- DTO는 `record` 또는 뼈대에 이미 쓰인 형태를 따른다 — 임의로 Lombok을 끼워넣지 않는다.
- 커밋 메시지: `feat|fix|docs|refactor|test: <한국어 한 줄>` + 관련 레벨 표기 (예: `feat: Lv 7 목록 조회 구현`).

## 5. 금지 동작

- `git push`, `git commit`, 브랜치 삭제 — **사용자가 명시적으로 요청할 때만**.
- `src/main/resources/static/**` 수정 — 완성된 프론트엔드다. 절대 손대지 않는다.
- `application.properties` 의 실제 DB 비밀번호를 커밋 — `.gitignore` 확인 후 작업.
- `docs/images/**` 삭제 — 과제 명세가 참조한다.

## 6. 디렉터리 계약

| 경로 | 소유 | 용도 |
| --- | --- | --- |
| `AGENTS.md` | 공용 | **이 규약. SSOT** |
| `CLAUDE.md` | Claude Code | AGENTS.md import + Claude 전용 보강 |
| `.antigravity/rules/` | Antigravity | AGENTS.md 포인터 |
| `.agents/playbooks/` | 공용 | 툴 중립 절차서. Skill/Workflow가 **공유해서 참조** |
| `.agents/mcp/servers.json` | 공용 | MCP 서버 정의 **SSOT** |
| `.agents/scripts/` | 공용 | 동기화 스크립트 |
| `.agents/handoff/` | 공용 | **인수인계**. 에이전트가 읽고 쓴다 |
| `.claude/skills/` | Claude Code | playbook을 감싼 얇은 래퍼 |
| `docs/` | 사람 | 사람이 읽는 문서 (ADR 등) |

원칙: **`.agents/` = 에이전트가 읽고 쓰는 공간, `docs/` = 사람이 읽는 공간.**

## 7. 절차서 (Playbooks)

작업 전 해당 절차서를 읽는다. 툴이 무엇이든 **같은 파일**을 읽는다.

- `.agents/playbooks/docker-mysql.md` — MySQL 컨테이너 기동 / 접속 확인
- `.agents/playbooks/level-work.md` — Lv N 한 단계를 처리하는 표준 루프
- `.agents/playbooks/api-contract-check.md` — 응답이 API 명세와 일치하는지 검증
- `.agents/playbooks/handoff.md` — 세션 종료 시 인수인계 작성
