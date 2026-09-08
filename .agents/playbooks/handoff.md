# Playbook: 인수인계 작성

> 툴 중립. Claude Code `/handoff`, Antigravity 모두 이 절차를 따른다.
> **다음 세션의 에이전트는 사람이 아니다.** 맥락을 추측할 수 없으니 사실만 명확히 남긴다.

## 언제
- 세션을 끝낼 때
- 툴을 바꿀 때 (Claude Code ↔ Antigravity) — **가장 중요한 순간**
- 한 레벨을 완료했을 때

## 절차

### 1. 사실 수집 (추측 금지)
```bash
git status --short
git log --oneline -5
./gradlew test
grep -rn "TODO (Lv" src/main/java
```

### 2. `.agents/handoff/CURRENT.md` 를 갱신
- **덮어쓴다.** 누적하지 않는다. 이 파일은 "지금 상태" 하나만 담는다.
- 진행표의 레벨 상태를 실제 코드와 대조해 갱신한다.
- "다음에 할 일"은 **하나**만, 실행 가능한 문장으로 쓴다.

### 3. 세션 기록을 남긴다
`.agents/handoff/log/YYYY-MM-DD-<주제>.md` 로 `TEMPLATE.md` 를 채운다.
이건 누적된다. 나중에 "왜 이렇게 했더라"를 여기서 찾는다.

### 4. 결정이 있었다면 ADR
설계 판단(예: 프로젝션 방식 선택)을 했다면 `docs/decisions/` 에 한 장 남긴다.

## 작성 원칙
- **검증되지 않은 것을 "완료"라고 쓰지 않는다.** `./gradlew test` 를 돌렸는지 명시한다.
- 실패한 시도도 적는다. 다음 에이전트가 같은 벽에 부딪히지 않게.
- 파일 경로는 `src/main/java/com/gamebasic/game/service/GameService.java:87` 형식으로 줄 번호까지.
- 사용자가 내린 결정(비밀번호 정책, 모드 변경 등)은 반드시 기록한다.
