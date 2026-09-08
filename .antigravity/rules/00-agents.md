---
trigger: always_on
description: 프로젝트 공통 에이전트 규약 (AGENTS.md 위임)
---

# 규칙: AGENTS.md 를 따른다

이 워크스페이스의 **모든** 에이전트 규칙은 저장소 루트의 `AGENTS.md` 한 파일에 있다.

**작업을 시작하기 전에 `AGENTS.md` 전문을 읽어라.** 요약본을 신뢰하지 마라.

특히 다음을 놓치지 마라:

- `AGENTS.md` §0 — 세션 시작 시 `.agents/handoff/CURRENT.md` 를 먼저 읽는다.
- `AGENTS.md` §2 — `COACH_MODE`. 이 저장소는 학습 과제다.
- `AGENTS.md` §3 — 절대 규칙. 위반 시 과제가 오답 처리된다.
- `AGENTS.md` §5 — 금지 동작 (`src/main/resources/static/**` 수정 금지 등).

작업 절차서는 `.agents/playbooks/` 에 있다. Claude Code의 Skill과 **같은 파일**이므로,
절차를 고칠 일이 생기면 이 폴더가 아니라 `.agents/playbooks/` 를 고친다.

세션을 끝낼 때는 `.agents/playbooks/handoff.md` 에 따라 인수인계를 남긴다.
