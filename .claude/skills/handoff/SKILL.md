---
name: handoff
description: 세션을 끝내며 인수인계를 남긴다. "인수인계", "정리하고 끝내자", "핸드오프", Antigravity로 넘어가기 전, 또는 한 레벨을 끝냈을 때 사용한다.
---

# 인수인계

절차는 `.agents/playbooks/handoff.md` 에 있다. **그 파일을 읽고 그대로 따른다.**
(Antigravity와 공유하는 파일이므로, 절차를 고칠 일이 생기면 이 스킬이 아니라 playbook을 고친다.)

산출물 두 가지:
- `.agents/handoff/CURRENT.md` — **덮어쓴다.** 지금 상태 하나만.
- `.agents/handoff/log/YYYY-MM-DD-<주제>.md` — 누적. `TEMPLATE.md` 를 채운다.

검증하지 않은 것을 "완료"로 쓰지 않는다. `./gradlew test` 결과를 실제로 확인하고 적는다.
