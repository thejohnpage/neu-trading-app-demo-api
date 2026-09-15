#!/usr/bin/env bash
set -euo pipefail

REPO_URL="${REPO_URL:-https://github.com/thejohnpage/neu-trading-app-demo-api.git}"
INSTALL_DIR="${INSTALL_DIR:-$HOME/neu-trading-app-demo-api}"
DB_PORT="${DB_PORT:-55432}"
KAFKA_PORT="${KAFKA_PORT:-9092}"

need() { command -v "$1" >/dev/null 2>&1 || { echo "ERROR: required command '$1' is not installed." >&2; exit 1; }; }

need git
need docker

echo "==> Checking Docker"
docker version >/dev/null

echo "==> Cloning/updating API repository"
if [[ -d "$INSTALL_DIR/.git" ]]; then
  git -C "$INSTALL_DIR" pull --ff-only
else
  git clone "$REPO_URL" "$INSTALL_DIR"
fi

cd "$INSTALL_DIR"

if [[ ! -f .env ]]; then
  cat > .env <<EOF
TRADING_DB_NAME=trading_demo
TRADING_DB_USER=trading_demo
TRADING_DB_PASSWORD=trading_demo_change_me
TRADING_DB_PORT=$DB_PORT
KAFKA_PORT=$KAFKA_PORT
API_PORT=8080
EOF
  echo "==> Created .env. For anything beyond a classroom VM, change TRADING_DB_PASSWORD."
fi

echo "==> Starting isolated PostgreSQL and Kafka containers"
docker compose -f deploy/docker-compose.vm.yml --env-file .env up -d postgres kafka

echo "==> Waiting for PostgreSQL"
for i in {1..30}; do
  if docker compose -f deploy/docker-compose.vm.yml --env-file .env exec -T postgres pg_isready -U "$(grep '^TRADING_DB_USER=' .env | cut -d= -f2)" >/dev/null 2>&1; then
    echo "PostgreSQL is ready."
    break
  fi
  [[ "$i" == 30 ]] && { echo "ERROR: PostgreSQL did not become ready." >&2; exit 1; }
  sleep 2
done

cat <<EOF

Infrastructure installed.
Repository: $INSTALL_DIR
PostgreSQL host port: $(grep '^TRADING_DB_PORT=' .env | cut -d= -f2)
Kafka host port: $(grep '^KAFKA_PORT=' .env | cut -d= -f2)

Next:
  cd $INSTALL_DIR
  ./deploy/linux/deploy.sh
EOF
