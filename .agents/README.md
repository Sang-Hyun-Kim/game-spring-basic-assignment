# `.agents/` — 에이전트 공용 작업 공간

**툴 중립.** Claude Code와 Antigravity가 **같은 파일**을 읽는다.
툴이 하나 더 늘어도 이 폴더는 그대로 두고 얇은 포인터만 추가하면 된다.

```
.agents/
├── mcp/servers.json      MCP 서버 정의 SSOT — 여기만 손으로 고친다
├── scripts/sync-mcp.mjs  servers.json → 각 툴 설정으로 배포
├── playbooks/            절차서. Skill/Workflow가 공유해서 참조
└── handoff/
    ├── CURRENT.md        지금 상태. 세션 시작 시 필독. 덮어쓴다
    ├── TEMPLATE.md       세션 기록 양식
    └── log/              세션 기록. 누적된다
```

## 원칙

1. **`.agents/` = 에이전트가 읽고 쓰는 공간, `docs/` = 사람이 읽는 공간.**
2. **규칙은 `AGENTS.md` 한 곳.** `CLAUDE.md` 와 `.antigravity/rules/` 는 포인터일 뿐이다.
3. **절차는 `playbooks/` 한 곳.** `.claude/skills/*/SKILL.md` 는 래퍼일 뿐이다.
   절차를 고칠 일이 생기면 래퍼가 아니라 playbook을 고친다.
4. **생성물은 손으로 고치지 않는다.** `.mcp.json` 이 그렇다.

## 툴이 각각 읽는 것

| | 규칙 | 절차 | 인수인계 | MCP |
| --- | --- | --- | --- | --- |
| Claude Code | `CLAUDE.md` → `@AGENTS.md` | `.claude/skills/` → playbook | `CURRENT.md` | `.mcp.json` (생성물) |
| Antigravity | `.antigravity/rules/00-agents.md` → `AGENTS.md` | playbook 직접 | `CURRENT.md` | `~/.antigravity/mcp_config.json` (생성물) |
