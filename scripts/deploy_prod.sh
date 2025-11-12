#!/usr/bin/env bash
set -euo pipefail

# -------- CONFIG --------
WAR_NAME="orderflow-api.war"  # final target context path = /orderflow-api
REMOTE_HOST="zitatusi.myhostpoint.ch"
REMOTE_USER="zitatusi"
REMOTE_TOMCAT_DIR="/app/tools/tomcat/apache-tomcat-10.1.33"
REMOTE_WEBAPPS="${REMOTE_TOMCAT_DIR}/webapps"
REMOTE_BIN="${REMOTE_TOMCAT_DIR}/bin"

# The built artifact Jenkins archives: target/orderflow-cloud-backend.war
# We copy/rename it to orderflow-api.war for the correct context path.
LOCAL_ARTIFACT="${1:-target/orderflow-cloud-backend.war}"

echo "[deploy] Using artifact: ${LOCAL_ARTIFACT}"
test -f "${LOCAL_ARTIFACT}" || { echo "Artifact not found"; exit 1; }

echo "[deploy] Stopping remote Tomcat..."
ssh -o StrictHostKeyChecking=no "${REMOTE_USER}@${REMOTE_HOST}" "bash -lc '
  set -e
  ${REMOTE_BIN}/shutdown.sh || true
  # wait up to ~20s for webapps to unlock
  for i in {1..20}; do
    if pgrep -f \"org.apache.catalina.startup.Bootstrap\" >/dev/null; then
      sleep 1
    else
      break
    fi
  done
  # remove old exploded dir + old war
  rm -rf ${REMOTE_WEBAPPS}/orderflow-api
  rm -f  ${REMOTE_WEBAPPS}/${WAR_NAME}
'"

echo "[deploy] Copying WAR to remote as ${WAR_NAME}..."
scp -o StrictHostKeyChecking=no "${LOCAL_ARTIFACT}" "${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_WEBAPPS}/${WAR_NAME}"

echo "[deploy] Starting remote Tomcat..."
ssh -o StrictHostKeyChecking=no "${REMOTE_USER}@${REMOTE_HOST}" "bash -lc '
  # ensure prod profile is set, if you use setenv.sh put it there; here is a safe check:
  if [ ! -f ${REMOTE_BIN}/setenv.sh ]; then
    echo \"export SPRING_PROFILES_ACTIVE=prod\" > ${REMOTE_BIN}/setenv.sh
    chmod +x ${REMOTE_BIN}/setenv.sh
  fi
  ${REMOTE_BIN}/startup.sh
'"

echo "[deploy] Verifying startup (poll simple endpoint)..."
# Adjust the health URL if you prefer /actuator/health
for i in {1..30}; do
  sleep 1
  if curl -fsS "https://${REMOTE_HOST}/orderflow-api/actuator/health" >/dev/null; then
    echo "[deploy] App is up."
    exit 0
  fi
done

echo "[deploy] WARNING: App did not respond in time. Check logs."
exit 1