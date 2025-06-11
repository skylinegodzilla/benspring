#!/bin/bash
set -euo pipefail

# ---------- Config ----------
IMAGE_NAME="my-spring-app"
IMAGE_TAG="0.0.4" # remember to update the tag
TAR_FILE="${IMAGE_NAME//[:\/]/_}_${IMAGE_TAG}.tar"
REMOTE_USER="skylinegodzilla"
REMOTE_HOST="192.168.1.236"
REMOTE_DIR="/home/$REMOTE_USER/DockerImages"
REMOTE_IMAGE_PATH="$REMOTE_DIR/$TAR_FILE"

# Set to match your container name in Portainer
CONTAINER_NAME="my-spring-app"  # must match actual container name running in Portainer

# ---------- Helpers ----------
function log() {
  echo "[$(date +'%Y-%m-%d %H:%M:%S')] $*"
}

# ---------- Check if image exists ----------
if ! docker image inspect "${IMAGE_NAME}:${IMAGE_TAG}" > /dev/null 2>&1; then
  echo "❌ ERROR: Docker image '${IMAGE_NAME}:${IMAGE_TAG}' not found."
  echo "➡️  Please double-check that you have updated the tag in the script."
  exit 1
fi

# ---------- Main ----------
log "Saving Docker image ${IMAGE_NAME}:${IMAGE_TAG} as ${TAR_FILE}..."
docker save -o "$TAR_FILE" "${IMAGE_NAME}:${IMAGE_TAG}"

log "Copying ${TAR_FILE} to ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR} ..."
scp "$TAR_FILE" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR"

log "Upload complete."

log "Connecting to server to load image into Docker and restart container..."
ssh "$REMOTE_USER@$REMOTE_HOST" << EOF
  set -e
  echo "Loading Docker image from $REMOTE_IMAGE_PATH..."
  docker load -i "$REMOTE_IMAGE_PATH"
  echo "✅ Image loaded."
EOF

log "🎉 Upload, and loaded into docker on the server. rember to restart in portainer."