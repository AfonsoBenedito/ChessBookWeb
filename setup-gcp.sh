#!/usr/bin/env bash
# One-time GCP setup for ChessBookWeb Cloud Run deployment.
# Run this once from a machine with gcloud authenticated as a project owner.
#
# Usage: bash setup-gcp.sh

set -euo pipefail

PROJECT_ID="afonso-benedito"
REGION="europe-southwest1"
SA_NAME="github-actions"
SA_EMAIL="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
REGISTRY_REPO="chessbookweb"
POOL_ID="github"
PROVIDER_ID="github-provider"
GITHUB_REPO="AfonsoBenedito/ChessBookWeb"

echo "==> Fetching project number..."
PROJECT_NUMBER=$(gcloud projects describe "$PROJECT_ID" --format="value(projectNumber)")

echo "==> Enabling required APIs..."
gcloud services enable \
  run.googleapis.com \
  artifactregistry.googleapis.com \
  iam.googleapis.com \
  iamcredentials.googleapis.com \
  --project="$PROJECT_ID"

echo "==> Creating Artifact Registry repository..."
gcloud artifacts repositories create "$REGISTRY_REPO" \
  --repository-format=docker \
  --location="$REGION" \
  --project="$PROJECT_ID" || echo "    (already exists, skipping)"

echo "==> Creating service account..."
gcloud iam service-accounts create "$SA_NAME" \
  --display-name="GitHub Actions" \
  --project="$PROJECT_ID" || echo "    (already exists, skipping)"

echo "==> Granting IAM roles..."
for ROLE in roles/run.admin roles/artifactregistry.writer roles/iam.serviceAccountUser; do
  gcloud projects add-iam-policy-binding "$PROJECT_ID" \
    --member="serviceAccount:${SA_EMAIL}" \
    --role="$ROLE" \
    --quiet
done

echo "==> Creating Workload Identity Pool..."
gcloud iam workload-identity-pools create "$POOL_ID" \
  --location="global" \
  --display-name="GitHub Actions" \
  --project="$PROJECT_ID" || echo "    (already exists, skipping)"

echo "==> Creating OIDC provider..."
gcloud iam workload-identity-pools providers create-oidc "$PROVIDER_ID" \
  --location="global" \
  --workload-identity-pool="$POOL_ID" \
  --issuer-uri="https://token.actions.githubusercontent.com" \
  --attribute-mapping="google.subject=assertion.sub,attribute.repository=assertion.repository,attribute.actor=assertion.actor,attribute.ref=assertion.ref" \
  --project="$PROJECT_ID" || echo "    (already exists, skipping)"

echo "==> Binding GitHub repo to service account..."
gcloud iam service-accounts add-iam-policy-binding "$SA_EMAIL" \
  --project="$PROJECT_ID" \
  --role="roles/iam.workloadIdentityUser" \
  --member="principalSet://iam.googleapis.com/projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${POOL_ID}/attribute.repository/${GITHUB_REPO}"

# Generate a random session secret
SESSION_SECRET=$(openssl rand -hex 32)

echo ""
echo "=========================================="
echo " GCP setup complete. Add these 3 secrets"
echo " to your GitHub repo:"
echo " https://github.com/${GITHUB_REPO}/settings/secrets/actions"
echo "=========================================="
echo ""
echo "  WIF_PROVIDER"
echo "  projects/${PROJECT_NUMBER}/locations/global/workloadIdentityPools/${POOL_ID}/providers/${PROVIDER_ID}"
echo ""
echo "  WIF_SERVICE_ACCOUNT"
echo "  ${SA_EMAIL}"
echo ""
echo "  SESSION_SECRET"
echo "  ${SESSION_SECRET}"
echo ""
