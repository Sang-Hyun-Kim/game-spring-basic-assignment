# CLAUDE.md

<!--
  이 파일에는 규칙을 적지 않는다.
  공통 규약은 AGENTS.md 하나뿐이며, Antigravity도 같은 파일을 읽는다.
  여기에는 "Claude Code에서만 의미 있는 것"만 적는다.
-->

@AGENTS.md

## Claude Code 전용

### 세션 시작
`.agents/handoff/CURRENT.md` 를 먼저 읽는다. (AGENTS.md §0)

### Skills
아래 스킬은 `.agents/playbooks/` 의 절차서를 그대로 실행한다. Antigravity와 내용이 공유된다.

| 스킬 | 언제 |
| --- | --- |
| `/level-work` | ASSIGNMENT.md의 Lv N 한 단계를 진행할 때 |
| `/api-contract-check` | API 응답이 명세와 맞는지 확인할 때 |
| `/handoff` | 세션을 끝내며 인수인계를 남길 때 |

### MCP
`.mcp.json` 은 **생성물**이다. 직접 고치지 말고 `.agents/mcp/servers.json` 을 고친 뒤:

```bash
node .agents/scripts/sync-mcp.mjs
```

### 자주 쓰는 명령

```bash
./gradlew test          # 커밋 전 필수
./gradlew bootRun       # 서버 기동 (MySQL 먼저)
docker compose -f .agents/docker/compose.yml up -d   # 없으면 playbook 참고
```
