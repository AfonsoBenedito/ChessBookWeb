#!/bin/bash
set -e

# --- Check Project ID ---
PROJECT_ID=$(gcloud config get-value project)
if [ -z "$PROJECT_ID" ]; then
    echo "Error: No Google Cloud Project ID set."
    exit 1
fi

BUCKET_NAME="${PROJECT_ID}-chess-db"

echo "WARNING: This will delete ALL data in gs://${BUCKET_NAME}"
echo "This is necessary to fix the 'corrupted data directory' error."
read -p "Are you sure? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    exit 1
fi

echo "Cleaning bucket by deleting it (Fastest)..."
# Using 'gcloud storage' is much faster than 'gsutil' for many files
# We delete the whole bucket; deploy.sh handles recreation.
gcloud storage rm --recursive "gs://${BUCKET_NAME}" || echo "Bucket not found (already deleted), continuing..."

echo "Bucket deleted. You can now run ./deploy.sh"
