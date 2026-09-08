#!/usr/bin/env node
/**
 * .agents/mcp/servers.json (SSOT) → 각 에이전트 툴의 MCP 설정으로 배포한다.
 *
 *   node .agents/scripts/sync-mcp.mjs            적용
 *   node .agents/scripts/sync-mcp.mjs --check    차이만 보고 (CI/커밋 훅용, 파일 안 건드림)
 *
 * 대상(target)
 *   claude-code  → <repo>/.mcp.json                  커밋됨. ${VAR} 를 그대로 둔다(Claude Code가 실행 시 치환).
 *   antigravity  → ~/.antigravity/mcp_config.json    커밋 안 됨. ${VAR} 를 지금 값으로 치환해서 쓴다.
 *
 * Antigravity 설정 경로가 다르면 환경변수로 덮어쓴다:
 *   ANTIGRAVITY_MCP_CONFIG=/path/to/mcp_config.json node .agents/scripts/sync-mcp.mjs
 */

import { readFileSync, writeFileSync, existsSync, mkdirSync } from 'node:fs';
import { homedir } from 'node:os';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const REPO = resolve(dirname(fileURLToPath(import.meta.url)), '..', '..');
const SSOT = join(REPO, '.agents', 'mcp', 'servers.json');
const CHECK = process.argv.includes('--check');

const ANTIGRAVITY_CANDIDATES = [
  process.env.ANTIGRAVITY_MCP_CONFIG,
  join(homedir(), '.antigravity', 'mcp_config.json'),
  join(homedir(), '.antigravity-ide', 'mcp_config.json'),
  join(homedir(), '.codeium', 'windsurf', 'mcp_config.json'),
].filter(Boolean);

/**
 * 경로 결정 우선순위:
 *   1) 설정 파일이 이미 있는 후보
 *   2) 상위 디렉터리가 이미 있는 후보 (그 툴이 설치된 흔적)
 *   3) 첫 번째 후보에 새로 만든다
 */
function antigravityPath() {
  return (
    ANTIGRAVITY_CANDIDATES.find(existsSync) ??
    ANTIGRAVITY_CANDIDATES.find((p) => existsSync(dirname(p))) ??
    ANTIGRAVITY_CANDIDATES[0]
  );
}

/** ${VAR} 를 process.env 값으로 치환. 값이 없으면 그대로 두고 경고한다. */
const missing = new Set();
function expand(node) {
  if (typeof node === 'string') {
    return node.replace(/\$\{([A-Z0-9_]+)\}/g, (whole, name) => {
      if (process.env[name]) return process.env[name];
      missing.add(name);
      return whole;
    });
  }
  if (Array.isArray(node)) return node.map(expand);
  if (node && typeof node === 'object') {
    return Object.fromEntries(Object.entries(node).map(([k, v]) => [k, expand(v)]));
  }
  return node;
}

function build(target, ssot) {
  const mcpServers = {};
  for (const [name, def] of Object.entries(ssot.servers)) {
    if (!def.targets.includes(target)) continue;
    mcpServers[name] = target === 'antigravity' ? expand(def.config) : def.config;
  }
  return { mcpServers };
}

/** 기존 파일에서 SSOT가 관리하지 않는 서버는 보존한다(글로벌 설정을 통째로 덮어쓰지 않기 위함). */
function merge(path, generated, managedNames) {
  if (!existsSync(path)) return generated;
  let existing;
  try {
    existing = JSON.parse(readFileSync(path, 'utf8'));
  } catch {
    console.warn(`  ! ${path} 파싱 실패 — 새로 씁니다`);
    return generated;
  }
  const kept = Object.fromEntries(
    Object.entries(existing.mcpServers ?? {}).filter(([n]) => !managedNames.has(n)),
  );
  return { ...existing, mcpServers: { ...kept, ...generated.mcpServers } };
}

function emit(label, path, content) {
  const next = JSON.stringify(content, null, 2) + '\n';
  const prev = existsSync(path) ? readFileSync(path, 'utf8') : null;
  if (prev === next) {
    console.log(`  = ${label.padEnd(12)} 변경 없음  ${path}`);
    return false;
  }
  if (CHECK) {
    console.log(`  ~ ${label.padEnd(12)} 갱신 필요  ${path}`);
    return true;
  }
  mkdirSync(dirname(path), { recursive: true });
  writeFileSync(path, next, 'utf8');
  console.log(`  + ${label.padEnd(12)} ${prev === null ? '생성' : '갱신'}      ${path}`);
  return true;
}

const ssot = JSON.parse(readFileSync(SSOT, 'utf8'));
const managed = new Set(Object.keys(ssot.servers));

console.log(`MCP SSOT: ${SSOT}`);
let drift = false;
drift = emit('claude-code', join(REPO, '.mcp.json'), build('claude-code', ssot)) || drift;

const agPath = antigravityPath();
drift = emit('antigravity', agPath, merge(agPath, build('antigravity', ssot), managed)) || drift;

if (missing.size) {
  console.warn(`\n  ! 환경변수 미설정: ${[...missing].join(', ')}`);
  console.warn('    Antigravity 설정에 치환되지 않은 채로 남았습니다.');
  console.warn('    IntelliJ > Settings > Tools > MCP Server 에서 토큰을 복사해 설정하세요:');
  console.warn('      PowerShell:  [Environment]::SetEnvironmentVariable("IJ_MCP_AUTH_TOKEN","<토큰>","User")');
}

if (CHECK && drift) {
  console.error('\n설정이 SSOT와 다릅니다. `node .agents/scripts/sync-mcp.mjs` 를 실행하세요.');
  process.exit(1);
}
