# 문서 지도

**`docs/` 는 사람이 읽는 공간이다.** 에이전트가 읽고 쓰는 것은 `.agents/` 에 있다.

| 찾는 것 | 위치 |
| --- | --- |
| 과제 요구사항 (Lv 1~12) | `../ASSIGNMENT.md` |
| 에이전트 공통 규약 | `../AGENTS.md` |
| 지금 어디까지 했나 | `../.agents/handoff/CURRENT.md` |
| 과거 세션에 무슨 일이 있었나 | `../.agents/handoff/log/` |
| 작업 절차서 | `../.agents/playbooks/` |
| MCP 연동 방법 | `../.agents/mcp/README.md` |
| 설계 결정과 근거 | `decisions/` |
| 과제 명세용 스크린샷 | `images/` — **삭제 금지** |

## ADR (`decisions/`)

되돌리기 어려운 판단을 남긴다. 파일명 `NNNN-제목.md`, 번호는 이어서 붙인다.
"왜 그 선택을 했는지"가 핵심이다. 코드만 봐서는 알 수 없는 것을 적는다.

예: 프로젝션 방식 선택, 트랜잭션 경계, 삭제 순서, 외부 API 실패 시 동작.
