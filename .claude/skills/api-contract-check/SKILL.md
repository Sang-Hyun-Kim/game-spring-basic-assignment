---
name: api-contract-check
description: API 응답이 명세와 정확히 일치하는지 검증한다. "명세랑 맞는지 확인", "필드 이름 맞나", "서버 응답이 API 명세와 다릅니다" 에러가 나올 때 사용한다.
---

# API 명세 일치 검증

절차는 `.agents/playbooks/api-contract-check.md` 에 있다. **그 파일을 읽고 그대로 따른다.**
(Antigravity와 공유하는 파일이므로, 절차를 고칠 일이 생기면 이 스킬이 아니라 playbook을 고친다.)

핵심: 키 이름을 **눈으로 훑지 말고 기계적으로 뽑아서** 비교한다. 어긋나면 DTO를 고친다.
