#!/usr/bin/env bash
#
# OrderFlow Cloud — production deploy helper (Hostpoint)
# - Builds the WAR (optional)
# - Uploads to Hostpoint via scp
# - Atomically replaces the WAR in Tomcat webapps
# - Prints the MANUAL restart commands (supervisorctl) you must run
# - Optional verify step after you restarted Tomcat
#
# NOTE (Hostpoint): supervisorctl stop/start tomcat CANNOT be run from this script.
# You must run those commands manually after upload.
#
# Usage examples:
#   ./scripts/deploy_prod.sh                     # build + upload + print restart cmds
#   ./scripts/deploy_prod.sh --build             # same as default (build + upload)
#   ./scripts/deploy_prod.sh --upload-only       # only upload last-built WAR
#   ./scripts/deploy_prod.sh --verify-only       # only verify URLs (after manual restart)
#   ./scripts/deploy_prod.sh --no-verify         # skip verify step
#   ./scripts/deploy_prod.sh --dry-run           # show what would run (no changes)
#
set -euo pipefail

############################
# Config — EDIT IF NEEDED  #
############################

# Git repo root relative path assumptions:
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Local build settings
MVN_BIN="${MVN_BIN:-mvn}"
MVN_GOALS="${MVN_GOALS:-clean verify}"
MVN_FLAGS="${MVN_FLAGS:--B -U -DskipTests=false}"

# Artifact (we build a WAR named orderflow-api.war)
LOCAL_TARGET_DIR="$REPO_ROOT/target"
LOCAL_WAR_NAME="orderflow-api.war"
LOCAL_WAR="$LOCAL_TARGET_DIR/$LOCAL_WAR_NAME"

# Remote SSH
REMOTE_HOST="${REMOTE_HOST:-zitatusi.myhostpoint.ch}"
REMOTE_USER="${REMOTE_USER:-zitatusi}"
REMOTE_SSH="${REMOTE_USER}@${REMOTE_HOST}"

# Remote Tomcat (CORRECT path with ~ per Hostpoint home)
REMOTE_TOMCAT_DIR="${REMOTE_TOMCAT_DIR:-~/app/tools/tomcat/apache-tomcat-10.1.33}"
REMOTE_WEBAPPS="${REMOTE_TOMCAT_DIR}/webapps"

# Remote artifact name (context path is /orderflow-api)
REMOTE_WAR_NAME="orderflow-api.war"
REMOTE_WAR_TMP="${REMOTE_WEBAPPS}/${REMOTE_WAR_NAME}.tmp"
REMOTE_WAR="${REMOTE_WEBAPPS}/${REMOTE_WAR_NAME}"

# Verify URLs (public)
VERIFY_BASE="https://devprojects.ch/orderflow-api"
VERIFY_PING="${VERIFY_BASE}/api/ping"
VERIFY_HEALTH="${VERIFY_BASE}/actuator/health"
VERIFY_SWAGGER="${VERIFY_BASE}/swagger-ui/index.html"

# SSH/SCP options (safe defaults)
SSH_OPTS=("-o" "BatchMode=yes" "-o" "StrictHostKeyChecking=accept-new")
SCP_OPTS=("-p" "-q" "-o" "StrictHostKeyChecking=accept-new")

#################################
# CLI flags (default: build+up) #
#################################
DO_BUILD=false
DO_UPLOAD=false
DO_VERIFY=true
DRY_RUN=false

if [[ $# -eq 0 ]]; then
  DO_BUILD=true
  DO_UPLOAD=true
else
  while [[ $# -gt 0 ]]; do
    case "$1" in
      --build)        DO_BUILD=true; DO_UPLOAD=true ;;
      --upload-only)  DO_UPLOAD=true; DO_BUILD=false ;;
      --verify-only)  DO_VERIFY=true; DO_BUILD=false; DO_UPLOAD=false ;;
      --no-verify)    DO_VERIFY=false ;;
      --dry-run)      DRY_RUN=true ;;
      -h|--help)
        cat <<EOF
Usage: $(basename "$0") [options]

Options:
  --build          Build WAR then upload (default mode if no args).
  --upload-only    Upload last built WAR only (no build).
  --verify-only    Only verify URLs after you manually restarted Tomcat.
  --no-verify      Skip the verify step.
  --dry-run        Print commands without executing them.
  -h, --help       Show this help.

Examples:
  $(basename "$0")               # build + upload + print manual restart cmds
  $(basename "$0") --upload-only # just upload
  $(basename "$0") --verify-only # verify endpoints after restart
EOF
        exit 0
        ;;
      *)
        echo "[WARN] Unknown option: $1" >&2
        ;;
    esac
    shift
  done
fi

run() {
  echo "+ $*"
  if [[ "$DRY_RUN" == "true" ]]; then
    return 0
  fi
  "$@"
}

require_file() {
  local f="$1"
  if [[ ! -f "$f" ]]; then
    echo "[ERROR] Required file not found: $f" >&2
    exit 1
  fi
}

say_manual_restart() {
  cat <<EOF

[MANUAL ACTION REQUIRED ON HOSTPOINT]
1) SSH into the server:
   ssh ${REMOTE_SSH}

2) Stop Tomcat via supervisorctl (Hostpoint requirement):
   supervisorctl stop tomcat

   (Optional but recommended) Remove exploded app dir if it exists (prevents stale files):
   rm -rf ${REMOTE_WEBAPPS}/orderflow-api/

3) Start Tomcat:
   supervisorctl start tomcat

4) Tail logs to watch startup:
   supervisorctl tail -f tomcat

After Tomcat is up, run verify locally:
   ${REPO_ROOT}/scripts/deploy_prod.sh --verify-only

EOF
}

verify_endpoint() {
  local url="$1" name="$2"
  echo "[VERIFY] $name -> $url"
  # Use curl fail/silent/show-error; allow 10s timeout
  if curl --fail --silent --show-error --max-time 10 "$url" > /dev/null; then
    echo "[OK] $name"
  else
    echo "[FAIL] $name" >&2
    return 1
  fi
}

#################
# Build (local) #
#################
if [[ "$DO_BUILD" == "true" ]]; then
  echo "[LOCAL] Building WAR..."
  run "$MVN_BIN" $MVN_FLAGS $MVN_GOALS
  echo "[LOCAL] Build done."

  if [[ ! -f "$LOCAL_WAR" ]]; then
    echo "[ERROR] WAR not found at $LOCAL_WAR" >&2
    echo "        Did the build succeed and produce ${LOCAL_WAR_NAME}?" >&2
    exit 1
  fi
fi

################
# Upload (scp) #
################
if [[ "$DO_UPLOAD" == "true" ]]; then
  echo "[DEPLOY] Uploading WAR to server..."
  require_file "$LOCAL_WAR"

  # ensure remote webapps exists (tilde path expands on remote)
  run ssh "${SSH_OPTS[@]}" "$REMOTE_SSH" "mkdir -p ${REMOTE_WEBAPPS}"

  # upload to .tmp then atomic mv
  run scp "${SCP_OPTS[@]}" "$LOCAL_WAR" "$REMOTE_SSH:${REMOTE_WAR_TMP}"
  echo "[DEPLOY] Atomic replace on remote..."
  run ssh "${SSH_OPTS[@]}" "$REMOTE_SSH" "mv -f ${REMOTE_WAR_TMP} ${REMOTE_WAR}"

  echo "[DEPLOY] WAR uploaded to ${REMOTE_WAR}"
  say_manual_restart
fi

############
# Verify   #
############
if [[ "$DO_VERIFY" == "true" ]]; then
  echo "[VERIFY] Checking endpoints (after you restarted Tomcat)..."
  any_fail=0

  verify_endpoint "$VERIFY_PING"    "Ping"      || any_fail=1
  verify_endpoint "$VERIFY_HEALTH"  "Actuator"  || any_fail=1
  verify_endpoint "$VERIFY_SWAGGER" "SwaggerUI" || any_fail=1

  if [[ $any_fail -eq 0 ]]; then
    echo "[VERIFY] All checks passed."
  else
    echo "[VERIFY] One or more checks failed. Inspect server logs and config." >&2
    exit 2
  fi
fi

echo "[DONE] deploy_prod.sh completed."