# GitHub Actions Workflows Setup Guide

## Overview
This project uses GitHub Actions for automated CI/CD pipelines. Below is the setup required to use all workflows.

## Available Workflows

### 1. **CI - Build & Test** (`ci.yml`)
- **Triggers**: Push to `main`/`develop`, Pull Requests
- **Tasks**:
  - Builds the application with Gradle
  - Runs unit tests
  - Uploads test reports on failure
  - Uploads JAR artifacts on success
- **Required Secrets**: Cloudinary credentials

### 2. **Docker - Build & Push** (`docker-build.yml`)
- **Triggers**: Push to `main` with changes to Dockerfile/source code
- **Tasks**:
  - Builds Docker image using Buildx
  - Pushes to GitHub Container Registry (GHCR)
  - Tags with branch, SHA, and `latest`
- **Permissions**: Write access to packages (automatic with GITHUB_TOKEN)

### 3. **Code Quality & Analysis** (`code-quality.yml`)
- **Triggers**: Push to `main`/`develop`, Pull Requests
- **Tasks**:
  - Runs SonarQube analysis (if configured)
  - Checks quality gates
- **Required Secrets**: 
  - `SONAR_HOST_URL` (optional)
  - `SONAR_TOKEN` (optional)

### 4. **Release - Create & Deploy** (`release.yml`)
- **Triggers**: Creation of version tags (`v*.*.*`) or manual workflow dispatch
- **Tasks**:
  - Builds the application
  - Creates GitHub Release
  - Uploads JAR as release asset
  - Builds and pushes Docker image with version tag
- **Permissions**: Write access to contents and packages

## Required GitHub Secrets

Set these in your repository settings: **Settings → Secrets and variables → Actions**

### Essential Secrets:
```
CLOUDINARY_CLOUD_NAME        # Your Cloudinary project name
CLOUDINARY_API_KEY           # Your Cloudinary API key
CLOUDINARY_API_SECRET        # Your Cloudinary API secret
```

### Optional Secrets (for Code Quality):
```
SONAR_HOST_URL               # SonarQube server URL
SONAR_TOKEN                  # SonarQube authentication token
```

## Setup Instructions

### Step 1: Add GitHub Secrets
1. Go to your repository on GitHub
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret** for each:
   - `CLOUDINARY_CLOUD_NAME`: Get from [Cloudinary Dashboard](https://console.cloudinary.com)
   - `CLOUDINARY_API_KEY`: Get from [Cloudinary Dashboard](https://console.cloudinary.com)
   - `CLOUDINARY_API_SECRET`: Get from [Cloudinary Dashboard](https://console.cloudinary.com)

### Step 2: Enable Docker Registry
1. Workflows automatically use `GITHUB_TOKEN` (no setup needed)
2. Docker images are pushed to `ghcr.io/your-username/scholarshelf`

### Step 3: (Optional) Configure SonarQube
1. Set up a SonarQube server or use [SonarCloud](https://sonarcloud.io)
2. Create a project and get your `SONAR_TOKEN`
3. Add secrets: `SONAR_HOST_URL` and `SONAR_TOKEN`

## Running Workflows

### Automatic Triggers:
- **CI**: On every push to `main`/`develop` or PR
- **Docker**: On push to `main` with code changes
- **Code Quality**: On every push to `main`/`develop` or PR

### Manual Trigger:
- **Release**: Go to Actions → Release → Run workflow → Enter version tag

## Example Usage

### Pushing Code:
```bash
git push origin main
# ↓ Automatically triggers CI and Code Quality workflows
```

### Creating a Release:
```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
# ↓ Automatically triggers Release workflow
# ↓ Creates GitHub Release and publishes JAR + Docker image
```

### Manual Release Dispatch:
1. Go to Actions → Release - Create & Deploy
2. Click "Run workflow"
3. Enter version tag (e.g., `v1.0.0`)

## Docker Image Access

After a successful Docker build, your image is available at:
```
ghcr.io/CodingABoveHEAd/ScholarShelf:latest
ghcr.io/CodingABoveHEAd/ScholarShelf:main
ghcr.io/CodingABoveHEAd/ScholarShelf:main-<sha>
```

Pull and run:
```bash
docker pull ghcr.io/CodingABoveHEAd/ScholarShelf:latest
docker run -e CLOUDINARY_API_SECRET=xxx ... ghcr.io/CodingABoveHEAd/ScholarShelf:latest
```

## Troubleshooting

### Workflow Fails on Database Connection
- Workflows use an in-memory or test PostgreSQL service
- Ensure `SPRING_DATASOURCE_*` env vars are set correctly in workflow files

### Docker Push Fails
- Verify `GITHUB_TOKEN` has `write:packages` permission
- Check repository Settings → Actions → General → Workflow permissions

### SonarQube Checks Fail
- If secrets aren't configured, SonarQube steps are skipped (non-blocking)
- Add `SONAR_HOST_URL` and `SONAR_TOKEN` to enable quality gates

## File Structure
```
.github/
├── workflows/
│   ├── ci.yml                  # Build & Test
│   ├── docker-build.yml        # Docker image build & push
│   ├── code-quality.yml        # SonarQube analysis
│   └── release.yml             # Release & deployment
sonar-project.properties        # SonarQube configuration
```

## Next Steps
1. ✅ Add repository secrets
2. ✅ Push to main branch to trigger CI
3. ✅ Create a release tag to test release workflow
4. ✅ (Optional) Configure SonarQube for code quality

---
For more info, see [GitHub Actions Documentation](https://docs.github.com/en/actions)
