# Playbook: Lv N 한 단계 처리하기

> 툴 중립. Claude Code `/level-work`, Antigravity 모두 이 절차를 따른다.

`AGENTS.md` §2 `COACH_MODE` 를 먼저 확인한다. `on` 이면 3단계에서 정답 코드를 쓰지 않는다.

## 표준 루프

### 1. 범위 확정
- `ASSIGNMENT.md` 에서 해당 `### Lv N` 절 **전문**을 읽는다. 체크박스가 곧 완료 조건이다.
- 그 절의 **API 명세 →** 링크가 가리키는 엔드포인트 명세를 확인한다.
- 관련 `TODO` 주석을 찾는다: `grep -rn "TODO (Lv N)" src/main/java`

### 2. 현재 실패를 재현
추측하지 말고 **실제 에러를 본다**.
```bash
./gradlew bootRun     # 또는 해당 API를 curl
```
스택트레이스에서 `com.gamebasic` 패키지 프레임을 찾는다. 거기가 문제 지점이다.

### 3. 수정
- `AGENTS.md` §3 절대 규칙을 위반하지 않는지 매번 대조한다.
- `COACH_MODE=on` 이면: 원인 + 접근 방향 + 검증 방법을 제시하고 멈춘다. 코드는 사용자가 쓴다.
- `COACH_MODE=off` 이거나 사용자가 구현을 명시 요청하면: 최소 변경으로 구현한다.

### 4. 검증
```bash
./gradlew test        # 반드시 성공
```
+ `.agents/playbooks/api-contract-check.md` 로 응답이 명세와 일치하는지 확인한다.
+ 해당 Lv의 "확인:" 항목을 실제로 재현한다. 화면 확인이 필요한 레벨은 사용자에게 확인을 요청한다.

### 5. 기록
`.agents/handoff/CURRENT.md` 의 진행표에서 해당 레벨 상태를 갱신한다.

## 레벨별 함정

| Lv | 놓치기 쉬운 것 |
| --- | --- |
| 1 | `ddl-auto` 값 — 재시작 후 데이터가 살아남아야 한다 |
| 3 | 경로를 명세와 **한 글자씩** 대조. 405는 "경로는 있고 메서드 매핑이 없다"는 뜻 |
| 4 | `Connection is read-only` → 트랜잭션 선언부 |
| 7 | 게임 목록은 `id` **내림차순**, `deck` 은 `RunCard.id` **오름차순** |
| 8 | 삭제는 자식(`RunCard`) 먼저. cascade 금지 |
| 9 | `isFinished()` 로 판정, 409, **데이터를 바꾸지 않고** 반환 |
| 10 | 서비스의 `ResponseStatusException` 을 제공된 예외 2개로 교체하는 것까지 |
| 11 | 쿼리 **2회** 고정. `select new` DTO 프로젝션. 인터페이스 프로젝션 금지 |
| 12 | 외부 API 필드명이 우리 응답과 다르다. 그대로 옮기지 말고 변환 |
