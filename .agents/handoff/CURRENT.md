# CURRENT — 지금 상태

> **모든 에이전트는 세션 시작 시 이 파일을 먼저 읽는다.** (`AGENTS.md` §0)
> 이 파일은 **덮어쓴다.** 누적 기록은 `.agents/handoff/log/` 에 남긴다.
> 이 파일과 코드가 다르면 **코드가 정답**이다. 이 파일을 고쳐라.

- **마지막 갱신**: 2026-09-08
- **갱신한 툴**: Claude Code
- **모드**: `COACH_MODE=on` (AGENTS.md §2)

## 다음에 할 일 (하나만)

**Lv 1 — `src/main/resources/application.properties` 작성.**
현재 이 파일이 **존재하지 않아** 서버가 뜨지 않는다.
`.agents/playbooks/docker-mysql.md` 를 따라 MySQL 컨테이너를 먼저 확인/기동한 뒤 datasource를 맞춘다.

## 진행표

| Lv | 내용 | 상태 |
| --- | --- | --- |
| 1 | 설정 파일 (Docker MySQL) | ⬜ `application.properties` 없음 |
| 2 | 빈 등록 / 의존성 주입 | ⬜ |
| 3 | RESTful 경로 (`GET /games`) | ⬜ |
| 4 | `@Transactional` 버그 | ⬜ |
| 5 | 요청 검증 + 응답 DTO | ⬜ TODO 2건 |
| 6 | 보상 카드 선택 / 진행 저장 | ⬜ TODO 1건 |
| 7 | 목록·상세 조회 | ⬜ TODO 3건 |
| 8 | 변경 감지 수정 / 자식부터 삭제 | ⬜ TODO 2건 |
| 9 | 끝난 게임 409 (도전) | ⬜ |
| 10 | 전역 예외 처리 (도전) | ⬜ TODO 1건 |
| 11 | N+1 없는 집계 + 저장 시간 (도전) | ⬜ TODO 1건 |
| 12 | 랭킹 (도전) | ⬜ |

## 남은 TODO (검증됨: `grep -rn "TODO (Lv" src/main/java`)

| 위치 | 레벨 |
| --- | --- |
| `common/exception/GlobalExceptionHandler.java:19` | Lv 10 |
| `game/controller/GameController.java:35` | Lv 6 |
| `game/repository/GameRepository.java:7` | Lv 7 |
| `game/service/GameService.java:87` | Lv 7 목록 |
| `game/service/GameService.java:92` | Lv 7 상세 |
| `game/service/GameService.java:97` | Lv 8 이름 변경 |
| `game/service/GameService.java:98` | Lv 8 삭제 |
| `runcard/dto/CardResponse.java:7` | Lv 5 |
| `runcard/dto/RunCardRequest.java:7` | Lv 5 |
| `runcard/repository/RunCardRepository.java:14` | Lv 11 |

## 환경 상태 (2026-09-08 확인)

- `git log`: `974bb83` (init 이후 docs 커밋 1건). **과제 코드 커밋 없음.**
- 미커밋 변경: `gradle/wrapper/*`, `gradlew*`, `settings.gradle` — Gradle Wrapper 갱신분.
- 테스트: `src/test/java/com/gamebasic/game/GameApiTests.java` 존재. **아직 실행해 확인하지 않음.**
- MySQL 컨테이너 기동 여부: **미확인.**
- IntelliJ MCP 서버: `127.0.0.1:64442` 리스닝 확인됨. 토큰(`IJ_MCP_AUTH_TOKEN`) 미설정.

## 열린 질문

- MySQL root 비밀번호 / 스키마 이름 — 사용자 결정 필요.
- `COACH_MODE` 를 계속 `on` 으로 둘지 — 사용자 확인 필요.
