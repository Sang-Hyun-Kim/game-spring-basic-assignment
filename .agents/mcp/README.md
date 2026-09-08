# MCP 공유 방식

## 왜 SSOT가 필요한가
Claude Code와 Antigravity는 **같은 JSON 스키마**(`mcpServers`)를 쓰지만 **파일 위치가 다르고**,
Antigravity 쪽은 프로젝트가 아니라 **사용자 전역**에 저장된다. 손으로 두 곳을 맞추면 반드시 어긋난다.

그래서 정의는 `servers.json` 한 곳에 두고, 스크립트가 각 툴 위치로 **배포**한다.

```
.agents/mcp/servers.json   ← 여기만 고친다 (커밋)
        │
        ├─ node .agents/scripts/sync-mcp.mjs
        │
        ├──→ <repo>/.mcp.json                    Claude Code  (커밋, ${VAR} 유지)
        └──→ ~/.antigravity/mcp_config.json      Antigravity  (커밋 안 함, ${VAR} 치환)
```

## 비밀값 취급

- `servers.json` 에는 **`${ENV_VAR}` 형태만** 적는다. 실제 토큰을 쓰면 커밋된다.
- `.mcp.json` 은 커밋되지만 `${VAR}` 그대로다 — Claude Code가 실행 시점에 치환한다.
- Antigravity 설정은 사용자 홈에 있고 커밋되지 않으므로, 스크립트가 **치환해서** 쓴다.

## IntelliJ (jetbrains) 서버 붙이기

1. IntelliJ 실행 → **Settings > Tools > MCP Server** 에서 포트와 토큰 확인.
   (이 저장소 기준 확인값: `127.0.0.1:64442`, 토큰 이름 `IJ_MCP_AUTH_TOKEN`)
2. 토큰을 환경변수로 등록한다. PowerShell:
   ```powershell
   [Environment]::SetEnvironmentVariable("IJ_MCP_AUTH_TOKEN","<토큰>","User")
   ```
   등록 후 **터미널과 IDE를 모두 재시작**해야 잡힌다.
3. 배포:
   ```bash
   node .agents/scripts/sync-mcp.mjs
   ```
4. 확인: `claude mcp list` 에 `jetbrains` 가 `✔ Connected` 로 보이면 성공.

> 옛 `npx @jetbrains/mcp-proxy` 방식은 포트 **64342** 로 붙는 구식 브리지다.
> IntelliJ 2025.2+ 는 내장 HTTP MCP 서버를 쓰므로 프록시가 필요 없다.

## 확인해야 할 것 (Antigravity)

이 저장소 세팅 시점에 Antigravity IDE는 설치만 되어 있고 **한 번도 실행되지 않아** 설정 파일이 없었다.
따라서 아래 경로는 **문서 기준 추정값**이다. 최초 1회 직접 확인할 것:

1. Antigravity 실행 → MCP 설정 화면에서 "Open raw config" 류 메뉴로 실제 경로를 본다.
2. 스크립트가 쓴 경로와 다르면 환경변수로 고정한다:
   ```bash
   ANTIGRAVITY_MCP_CONFIG="<실제 경로>" node .agents/scripts/sync-mcp.mjs
   ```
3. 확인된 경로를 이 문서에 적어 둔다. (추정 → 확정)

스크립트는 SSOT가 관리하지 않는 서버 항목은 **보존**하므로, Antigravity에서 따로 추가한 서버가 날아가지 않는다.
