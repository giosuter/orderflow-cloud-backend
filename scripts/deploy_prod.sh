#!/usr/bin/env bash
set -euo pipefail

################################################################
# OrderFlow Cloud — Production Deploy Script
# Target: Hostpoint Tomcat (shared), WAR context /orderflow-api
# NOTE: Hostpoint requires MANUAL restart via:
#   supervisorctl stop tomcat
#   supervisorctl start tomcat
# We DO NOT call supervisorctl from here.
################################################################

# --- Edit if needed ---
REMOTE_HOST="zitatusi@zitatusi.myhostpoint.ch"
REMOTE_TOMCAT_DIR="$HOME/app/tools/tomcat/apache-tomcat-10.1.33"
REMOTE_WEBAPPS="$REMOTE_TOMCAT_DIR/webapps"

LOCAL_WAR="target/orderflow-cloud-backend.war"
REMOTE_WAR_NAME="orderflow-api.war"   # final name -> context path /orderflow-api

VERIFY_HEALTH_URL="https://devprojects.ch/orderflow-api/actuator/health"
VERIFY_PING_URL="https://devprojects.ch/orderflow-api/api/ping"
# ----------------------

usage() {
  cat <<USAGE
Usage: $(basename "$0") [--build] [--no-build] [--upload-only] [--verify-only]
Default (no flags): --build + --upload-only

  --build         Run 'mvn clean package' to produce the WAR
  --no-build      Skip local build
  --upload-only   Copy WAR to server and atomically switch to ${REMOTE_WAR_NAME}
  --verify-only   Verify health & ping (assumes you've already restarted Tomcat MANUALLY)

IMPORTANT (manual step on Hostpoint):
  1) ssh ${REMOTE_HOST}
  2) supervisorctl stop tomcat
  3) supervisorctl start tomcat
Then run: $(basename "$0") --verify-only
USAGE
}

run_build() {
  echo "[LOCAL] Building WAR..."
  mvn -B -U -DskipTests=false clean package
  echo "[LOCAL] Build done."
}

upload_and_switch() {
  echo "[DEPLOY] Uploading WAR to server..."
  if [[ ! -f "$LOCAL_WAR" ]]; then
    echo "[ERROR] WAR not found at $LOCAL_WAR. Did you run --build?" >&2
    exit 2
  fi

  scp -C "$LOCAL_WAR" "${REMOTE_HOST}:${REMOTE_WEBAPPS}/${REMOTE_WAR_NAME}.tmp"

  ssh -o BatchMode=yes "$REMOTE_HOST" bash -s <<'EOSSH'
set -euo pipefail
REMOTE_TOMCAT_DIR="$HOME/app/tools/tomcat/apache-tomcat-10.1.33"
REMOTE_WEBAPPS="$REMOTE_TOMCAT_DIR/webapps"
REMOTE_WAR_NAME="orderflow-api.war"

# Remove exploded dir to force clean redeploy
rm -rf "$REMOTE_WEBAPPS/orderflow-api"

# Atomic move of the WAR
mv "$REMOTE_WEBAPPS/$REMOTE_WAR_NAME.tmp" "$REMOTE_WEBAPPS/$REMOTE_WAR_NAME"
echo "[REMOTE] WAR switched to $REMOTE_WEBAPPS/$REMOTE_WAR_NAME"
EOSSH

  cat <<MSG

[MANUAL ACTION REQUIRED ON HOSTPOINT NOW]

1) ssh ${REMOTE_HOST}
2) supervisorctl stop tomcat
3) supervisorctl start tomcat

After Tomcat is up, you can run:
  $(basename "$0") --verify-only

MSG
}

verify_only() {
  echo "[VERIFY] Waiting for health to be UP (max ~120s)..."
  for i in {1..60}; do
    STATUS=$(curl -fsS --max-time 5 "$VERIFY_HEALTH_URL" | tr -d '\n' || true)
    if echo "$STATUS" | grep -q '"status":"UP"'; then
      echo "[VERIFY] Health is UP."
      break
    fi
    sleep 2
  done

  # Final check
  curl -f "$VERIFY_HEALTH_URL" >/dev/null
  echo "[VERIFY] Health OK: $VERIFY_HEALTH_URL"

  echo -n "[VERIFY] Ping says: "
  curl -fsS "$VERIFY_PING_URL" || { echo "[VERIFY] Ping failed"; exit 3; }
  echo
  echo "[DONE] Verification completed."
}

# ---------- arg parsing ----------
DO_BUILD=false
DO_UPLOAD=false
DO_VERIFY=false

if [[ $# -eq 0 ]]; then
  DO_BUILD=true
  DO_UPLOAD=true
else
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --build)        DO_BUILD=true ;;
      --no-build)     DO_BUILD=false ;;
      --upload-only)  DO_UPLOAD=true ;;
      --verify-only)  DO_VERIFY=true ;;
      -h|--help)      usage; exit 0 ;;
      *) echo "Unknown arg: $1"; usage; exit 1 ;;
    esac
    shift
  done
fi

$DO_BUILD   && run_build
$DO_UPLOAD  && upload_and_switch
$DO_VERIFY  && verify_only

if ! $DO_UPLOAD && ! $DO_VERIFY && ! $DO_BUILD; then
  usage; exit 1
fi