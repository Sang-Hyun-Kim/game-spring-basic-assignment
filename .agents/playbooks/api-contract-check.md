# Playbook: API 명세 일치 검증

> 툴 중립. Claude Code `/api-contract-check`, Antigravity 모두 이 절차를 따른다.

## 왜
`AGENTS.md` §3: **API 경로 / JSON 필드명 / enum 값이 명세와 한 글자도 다르면 안 된다.**
프론트엔드는 완성본이라 고칠 수 없다. 서버가 명세에 맞춰야 한다.
`서버 응답이 API 명세와 다릅니다 (...)` 화면 메시지는 이 불일치의 신호다.

명세: https://f-api.github.io/game-spring-api-docs/basic/api-docs.html

## 절차

1. 대상 엔드포인트의 명세를 연다. 요청/응답 **JSON 예시**를 그대로 확보한다.

2. 실제 응답을 뜬다.
   ```bash
   curl -s http://localhost:8080/games | python -m json.tool
   ```

3. **키 이름만 뽑아서 기계적으로 비교한다.** 눈으로 훑지 않는다.
   ```bash
   curl -s http://localhost:8080/games/1 | python -c "import json,sys; print('\n'.join(sorted(json.load(sys.stdin).keys())))"
   ```

4. 대조 체크리스트
   - [ ] 경로 — 복수형 명사, `{gameId}` 위치
   - [ ] HTTP 메서드
   - [ ] 성공 상태 코드 (2xx면 됨)
   - [ ] 최상위 필드명 — **대소문자·축약형까지**
   - [ ] 중첩 객체/배열 필드명 (`deck[].id` 등)
   - [ ] enum 문자열 값 (`CLEARED`, `FAILED` …)
   - [ ] 배열 정렬 순서
   - [ ] 에러 응답 형식 — `status`, `error`, `message`, `path`

5. 어긋난 필드는 **DTO 쪽을 고친다.** 엔티티 필드명이나 프론트엔드를 건드리지 않는다.

## 서버 없이 확인하기
`./gradlew test` 의 `MockMvc` 테스트로 `jsonPath` 를 걸어 두면 회귀를 막을 수 있다.
