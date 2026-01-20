#!/bin/bash

clear

# ==============================================================
# 🛍️ ShopVerse – AUTO-SCALING LOCAL START SCRIPT
# ==============================================================
# Usage:
#   PRODUCT=3 USER=2 AUTH_SERVICE=1 ./start.sh
#
# Frontend  → http://localhost:8080
# Eureka     → http://localhost:8761
# ==============================================================

# ---------------- Resolve paths ----------------
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$ROOT" || exit 1

LOG_DIR="$ROOT/logs"
mkdir -p "$LOG_DIR"

echo "=============================================================="
echo " 🛍️  ShopVerse – AUTO-SCALING START"
echo "=============================================================="
echo ""

# ==============================================================
# HEALTH CHECK
# ==============================================================
wait_for_health () {
  local NAME=$1
  local URL=$2

  for i in {1..120}; do
    if curl -s "$URL" | grep -q '"status":"UP"'; then
      echo "✅ $NAME → HEALTHY"
      return 0
    fi
    sleep 1
  done

  echo "⚠️ $NAME → STARTED (health not confirmed)"
  return 0
}

# ==============================================================
# HASH-BASED SMART REBUILD
# ==============================================================
needs_rebuild () {
  local SERVICE_DIR=$1
  local HASH_FILE="$SERVICE_DIR/.build-hash"

  [ ! -d "$SERVICE_DIR/src" ] && return 1

  NEW_HASH=$(find "$SERVICE_DIR/src" -type f -print0 \
    | sort -z \
    | xargs -0 sha256sum \
    | sha256sum | awk '{print $1}')

  OLD_HASH=""
  [ -f "$HASH_FILE" ] && OLD_HASH=$(cat "$HASH_FILE")

  if [ "$NEW_HASH" != "$OLD_HASH" ]; then
    echo "$NEW_HASH" > "$HASH_FILE"
    return 0
  fi

  return 1
}

# ==============================================================
# SCALING CONFIGURATION
# ==============================================================

# Default replicas (if ENV not provided)
declare -A SCALE_DEFAULTS=(
  [auth-service]=1
  [user]=1
  [product]=2
  [order-Service]=1
  [payment-service]=1
  [notification]=1
  [analytics]=1
  [recommendation]=1
)

# Base ports (each instance increments)
declare -A BASE_PORTS=(
  [auth-service]=8081
  [user]=8082
  [product]=8083
  [order-Service]=8084
  [payment-service]=8085
  [notification]=8086
  [analytics]=8087
  [recommendation]=8088
  [gateway]=8080
)

# Read scale from ENV
get_scale () {
  local SERVICE=$1
  local ENV_KEY=$(echo "$SERVICE" | tr 'a-z-' 'A-Z_')
  echo "${!ENV_KEY:-${SCALE_DEFAULTS[$SERVICE]:-1}}"
}

# ==============================================================
# START DOCKER INFRA
# ==============================================================
echo "🐳 Starting Docker infrastructure..."
docker compose up -d kafka zookeeper mysql redis mongodb grafana prometheus zipkin >/dev/null 2>&1
sleep 5
echo "✅ Docker ready"
echo ""

# ==============================================================
# START DISCOVERY SERVICE (SEQUENTIAL)
# ==============================================================
DISCOVERY_NAME="discovery-service"
DISCOVERY_PORT=8761
DISCOVERY_DIR="$ROOT/$DISCOVERY_NAME"
DISCOVERY_JAR_DIR="$DISCOVERY_DIR/build/libs"

echo "▶️ Preparing discovery-service..."

if needs_rebuild "$DISCOVERY_DIR" || [ ! -f "$DISCOVERY_JAR_DIR"/*.jar ]; then
  echo "🔨 Rebuilding discovery-service"
  cd "$DISCOVERY_DIR" || exit 1
  chmod +x ./gradlew
  ./gradlew bootJar -x test --rerun-tasks >/dev/null 2>&1
  cd "$ROOT"
else
  echo "✅ discovery-service → no changes"
fi

DISCOVERY_JAR=$(ls "$DISCOVERY_JAR_DIR"/*.jar | head -n 1)

echo "🚀 Starting discovery-service..."
java -Xmx256m -Xms256m \
  -Dspring.profiles.active=dev \
  -Dserver.port=$DISCOVERY_PORT \
  -jar "$DISCOVERY_JAR" \
  > "$LOG_DIR/discovery-service.log" 2>&1 &

wait_for_health "discovery-service" "http://localhost:$DISCOVERY_PORT/actuator/health"

echo ""
echo "🚀 Discovery UP — starting services"
echo ""

# ==============================================================
# START ALL SERVICES (AUTO-SCALED)
# ==============================================================
for SERVICE in "${!BASE_PORTS[@]}"; do
  [[ "$SERVICE" == "gateway" ]] && continue

  SCALE=$(get_scale "$SERVICE")
  BASE_PORT=${BASE_PORTS[$SERVICE]}
  SERVICE_DIR="$ROOT/$SERVICE"
  JAR_DIR="$SERVICE_DIR/build/libs"

  echo "▶️ $SERVICE → scale = $SCALE"

  if needs_rebuild "$SERVICE_DIR" || [ ! -f "$JAR_DIR"/*.jar ]; then
    echo "🔨 Rebuilding $SERVICE"
    cd "$SERVICE_DIR" || continue
    chmod +x ./gradlew
    ./gradlew bootJar -x test --rerun-tasks >/dev/null 2>&1
    cd "$ROOT"
  else
    echo "✅ $SERVICE → no changes"
  fi

  JAR=$(ls "$JAR_DIR"/*.jar 2>/dev/null | head -n 1)
  [ ! -f "$JAR" ] && echo "⚠️ $SERVICE → JAR missing, skipping" && continue

  for ((i=0; i<SCALE; i++)); do
    PORT=$((BASE_PORT + i))
    LOG="$LOG_DIR/$SERVICE-$PORT.log"

    echo "🚀 Starting $SERVICE instance on port $PORT"

    java -Xmx384m -Xms256m \
      -Dspring.profiles.active=dev \
      -Dserver.port=$PORT \
      -jar "$JAR" \
      > "$LOG" 2>&1 &
  done
done

# ==============================================================
# START API GATEWAY (SINGLE INSTANCE)
# ==============================================================
GATEWAY_DIR="$ROOT/gateway"
GATEWAY_JAR=$(ls "$GATEWAY_DIR/build/libs"/*.jar | head -n 1)

echo ""
echo "🚪 Starting API Gateway on port 8080"

java -Xmx256m -Xms256m \
  -Dspring.profiles.active=dev \
  -Dserver.port=8080 \
  -jar "$GATEWAY_JAR" \
  > "$LOG_DIR/gateway.log" 2>&1 &

# ==============================================================
# HEALTH CHECK ALL INSTANCES
# ==============================================================
echo ""
echo "⏳ Verifying health of all instances..."
echo ""

for SERVICE in "${!BASE_PORTS[@]}"; do
  [[ "$SERVICE" == "gateway" ]] && continue

  SCALE=$(get_scale "$SERVICE")
  BASE_PORT=${BASE_PORTS[$SERVICE]}

  for ((i=0; i<SCALE; i++)); do
    PORT=$((BASE_PORT + i))
    wait_for_health "$SERVICE:$PORT" "http://localhost:$PORT/actuator/health" &
  done
done

wait

# ==============================================================
# DONE
# ==============================================================
echo ""
echo "=============================================================="
echo " ✅ ALL SERVICES STARTED (AUTO-SCALED)"
echo "=============================================================="
echo ""
echo "🔍 Eureka Dashboard : http://localhost:8761"
echo "🚪 API Gateway      : http://localhost:8080"
echo "📁 Logs             : logs/"
echo ""
echo "🛑 Stop services    : CTRL + C"
echo ""

wait
