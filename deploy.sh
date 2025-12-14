#!/bin/bash
set -e

# --- Configuration ---
SERVICE_NAME="chessbook-web"
REGION="europe-southwest1" # Change if needed

# --- Check Project ID ---
PROJECT_ID=$(gcloud config get-value project)
if [ -z "$PROJECT_ID" ]; then
    echo "Error: No Google Cloud Project ID set. Run 'gcloud config set project YOUR_PROJECT_ID' first."
    exit 1
fi
echo "Using Project ID: $PROJECT_ID"

BUCKET_NAME="${PROJECT_ID}-chess-db"
IMAGE_NAME="gcr.io/${PROJECT_ID}/${SERVICE_NAME}:latest"

# --- 1. Enable APIs ---
echo "Enabling necessary APIs..."
gcloud services enable run.googleapis.com \
    artifactregistry.googleapis.com \
    cloudbuild.googleapis.com \
    storage-component.googleapis.com

# --- 1.5 Grant Permissions ---
PROJECT_NUMBER=$(gcloud projects describe $PROJECT_ID --format='value(projectNumber)')
# Default Compute Service Account is used by Cloud Run by default
SERVICE_ACCOUNT="${PROJECT_NUMBER}-compute@developer.gserviceaccount.com"

echo "Granting Storage Admin to ${SERVICE_ACCOUNT}..."
# We use 'gsutil' or 'gcloud projects' to bind the role.
# Using project-level binding to be safe and simple:
gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:${SERVICE_ACCOUNT}" \
    --role="roles/storage.objectAdmin" > /dev/null

# --- 2. Create Storage Bucket (if not exists) ---
echo "Checking/Creating GCS Bucket for Database persistence..."
if ! gsutil ls -b "gs://${BUCKET_NAME}" > /dev/null 2>&1; then
    gsutil mb -l ${REGION} "gs://${BUCKET_NAME}"
    echo "Bucket gs://${BUCKET_NAME} created."
else
    echo "Bucket gs://${BUCKET_NAME} already exists."
fi

# --- 3. Build Container Image ---
echo "Building Docker Image..."
# We use Cloud Build to avoid local docker issues and network upload time
gcloud builds submit --tag ${IMAGE_NAME} ./code

# --- 4. Prepare Service YAML ---
echo "Preparing service.yaml..."
# Replace placeholder with actual Project ID
sed "s/PROJECT_ID_PLACEHOLDER/${PROJECT_ID}/g" service.yaml > service.deploy.yaml

# --- 5. Deploy to Cloud Run ---
echo "Deploying to Cloud Run..."
gcloud run services replace service.deploy.yaml --region=${REGION}

# Allow public access
echo "Permitting unauthenticated access..."
gcloud run services add-iam-policy-binding ${SERVICE_NAME} \
  --region=${REGION} \
  --member=allUsers \
  --role=roles/run.invoker

# --- 6. Cleanup ---
rm service.deploy.yaml

echo "Deployment Complete!"
echo "Your app should be available at the URL provided above."
echo "Note: The first request might take ~15s (Cold Start + DB Init)."
