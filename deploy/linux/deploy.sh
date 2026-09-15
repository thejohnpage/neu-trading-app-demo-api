#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

need() { command -v "$1" >/dev/null 2>&1 || { echo "ERROR: required command '$1' is not installed." >&2; exit 1; }; }
need git
need docker

[[ -f .env ]] || { echo "ERROR: .env missing. Run deploy/linux/install.sh first." >&2; exit 1; }

echo "==> Updating source"
git pull --ff-only

echo "==> Building and deploying API container"
docker compose -f deploy/docker-compose.vm.yml --env-file .env up -d --build

echo "==> Service status"
docker compose -f deploy/docker-compose.vm.yml --env-file .env ps

echo "==> Waiting for API health"
API_PORT="$(grep '^API_PORT=' .env | cut -d= -f2)"
for i in {1..40}; do
  if curl -fsS "http://localhost:${API_PORT}/actuator/health" >/dev/null 2>&1; then
    echo "API is healthy: http://localhost:${API_PORT}/actuator/health"
    echo "Version:"
    curl -fsS "http://localhost:${API_PORT}/api/v1/version" || true
    echo
    exit 0
  fi
  [[ "$i" == 40 ]] && break
  sleep 3
done

echo "ERROR: API did not become healthy. Recent logs:" >&2
docker compose -f deploy/docker-compose.vm.yml --env-file .env logs --tail=100 api >&2
exit 1
