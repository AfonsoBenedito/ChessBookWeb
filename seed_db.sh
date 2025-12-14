#!/bin/bash
set -e

# Configuration
SERVICE_NAME="chessbook-web"
PROJECT_ID=$(gcloud config get-value project)
BUCKET_NAME="${PROJECT_ID}-chess-db"
LOCAL_DB_DIR="./temp_db_data"

echo "Using Project ID: $PROJECT_ID"
echo "Bucket: $BUCKET_NAME"

# 1. Clean previous attempts
echo "Cleaning up local temp directory..."
rm -rf $LOCAL_DB_DIR
mkdir -p $LOCAL_DB_DIR

echo "Cleaning remote bucket..."
./cleanup_db.sh

# Recreate bucket (cleanup_db.sh deletes it)
echo "Recreating bucket gs://${BUCKET_NAME}..."
gsutil mb -l europe-southwest1 "gs://${BUCKET_NAME}" || echo "Bucket might already exist or creation failed."

# 2. Run MySQL locally to initialize data
echo "Starting local MySQL container to generate initial data..."
# We use the same image and arguments as the Cloud Run service to ensure compatibility
# reducing I/O flags are good but not strictly necessary locally, but good to match config.
docker run --rm --name temp-mysql-init \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -e MYSQL_DATABASE=chess \
  -v "$(pwd)/$LOCAL_DB_DIR:/var/lib/mysql" \
  -d mysql:8.0 \
  --default-authentication-plugin=mysql_native_password \
  --innodb-use-native-aio=0 \
  --innodb-doublewrite=0 \
  --innodb-flush-log-at-trx-commit=0 \
  --skip-log-bin \
  --sync-binlog=0 \
  --lower-case-table-names=1 \
  --innodb-file-per-table=0

echo "Waiting for MySQL to initialize (20s)..."
sleep 20

echo "Stopping MySQL container..."
docker stop temp-mysql-init

echo "Database initialized locally at $LOCAL_DB_DIR"

# 3. Upload to GCS
echo "Uploading initialized data to gs://${BUCKET_NAME}..."
# Use gcloud storage cp for performance (recursive)
gcloud storage cp -r "$LOCAL_DB_DIR/*" "gs://${BUCKET_NAME}/"

echo "Upload complete."
echo "Cleaning up local files..."
rm -rf $LOCAL_DB_DIR

echo "Success! The bucket is now pre-seeded. You can run ./deploy.sh"
