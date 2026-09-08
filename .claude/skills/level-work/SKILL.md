---
name: level-work
description: ASSIGNMENT.md의 Lv N 한 단계를 표준 루프로 진행한다. 사용자가 "Lv 7 하자", "다음 레벨", "이 단계 구현" 처럼 과제 레벨 작업을 요청할 때 사용한다.
---

# Lv N 작업

절차는 `.agents/playbooks/level-work.md` 에 있다. **그 파일을 읽고 그대로 따른다.**
(Antigravity와 공유하는 파일이므로, 절차를 고칠 일이 생기면 이 스킬이 아니라 playbook을 고친다.)

시작 전에 확인할 것:
1. `AGENTS.md` §2 `COACH_MODE` 값
2. `AGENTS.md` §3 절대 규칙
3. `.agents/handoff/CURRENT.md` 의 현재 진행 상태

인자로 레벨 번호가 오면 그 레벨을, 없으면 `CURRENT.md` 의 "다음에 할 일"을 대상으로 삼는다.
