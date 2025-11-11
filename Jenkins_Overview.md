# Jenkins Pipelines – OrderFlow Cloud

This document describes the Jenkins pipelines for **OrderFlow Cloud**, covering both **backend (Spring Boot WAR on Tomcat)** and **frontend (Angular static site)**.

---

## Jobs Overview

### Backend (WAR → Tomcat)
1. **orderflow-backend-deploy-local**  
   Builds the backend and deploys the WAR to the local Tomcat on the Mac where Jenkins runs. Useful for quick smoke tests.

2. **orderflow-backend-deploy-prod**  
   Builds, tests, and deploys the WAR to Hostpoint Tomcat. Runs smoke test via HTTPS.

3. **orderflow-backend-deploy-local-then-prod**  
   Combined pipeline: deploys to local first, and if successful, deploys to production. Guards production with a successful local deploy.

### Frontend (Angular → static web root)
4. **orderflow-frontend-build**  
   Checks out the Angular repository, runs `npm ci`, and builds the production bundle for verification. Archives build artifacts.

5. **orderflow-frontend-deploy-prod**  
   Builds the frontend and uploads the compiled files to Hostpoint web root (e.g., `/home/zitatusi/www/devprojects.ch/orderflow-cloud/`).

---

## Jenkins Prerequisites

- **Global Tools**
  - JDK 21 tool named: `jdk-21`
  - NodeJS tool named: `node-23` (Node 23.x as per your local environment)
- **Credentials**
  - SSH private key credential with ID: `hostpoint-ssh-key` (for `zitatusi@zitatusi.myhostpoint.ch`)

- **Agents/Nodes**
  - Run on the Jenkins master (localhost) is sufficient; ensure Maven and Git are installed on that machine.

---

## Deployment Paths (Production)

- **Tomcat**
  - `REMOTE_TOMCAT_DIR = /home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33`
  - `REMOTE_WEBAPPS    = /home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33/webapps`
  - Context path: `/orderflow-api` (WAR name `orderflow-api.war`)

- **Frontend (static)**
  - `REMOTE_WEBROOT = /home/zitatusi/www/devprojects.ch/orderflow-cloud/`
  - Public URL: `https://devprojects.ch/orderflow-cloud/`

Adjust the frontend path if you prefer `https://devprojects.ch/orderflow/` instead.

---

## How to Create the Jobs

1. In Jenkins, click **New Item** → **Pipeline**.
2. Use the following names for the five jobs:
   - `orderflow-backend-deploy-local`
   - `orderflow-backend-deploy-prod`
   - `orderflow-backend-deploy-local-then-prod`
   - `orderflow-frontend-build`
   - `orderflow-frontend-deploy-prod`
3. In **Pipeline** → **Definition**: “Pipeline script from SCM” if you commit these Jenkinsfiles into your repos, or “Pipeline script” to paste the content directly.
4. If using “from SCM”, place each Jenkinsfile at the root and set **Script Path** appropriately, e.g.:
   - `Jenkinsfile.backend.local`
   - `Jenkinsfile.backend.prod`
   - `Jenkinsfile.backend.local_then_prod`
   - `Jenkinsfile.frontend.build`
   - `Jenkinsfile.frontend.deploy_prod`
5. Add parameter `BRANCH` (default `main`) to each job so you can build different branches.

---

## Notes & Gotchas

- **Profiles**: The pipelines start Tomcat with `SPRING_PROFILES_ACTIVE=prod`. Ensure `application-prod.properties` is correct on production.
- **Smoke Tests**:
  - Backend: `curl https://devprojects.ch/orderflow-api/api/ping`
  - Frontend: a simple `GET https://devprojects.ch/orderflow-cloud/` returning 200 is enough.
- **Permissions**: The Hostpoint user `zitatusi` must have write permissions to the Tomcat webapps and the web root directories.
- **Tomcat Conflicts**: If several WARs fail at deploy time, it can block the Tomcat startup. Keep logs clean and fix broken apps quickly.
- **Angular Dist Folder**: The pipelines assume `dist/orderflow-frontend/browser`. If your Angular project name differs, adjust `ANGULAR_PROJECT` or `DIST_DIR`.
- **Node Version**: Ensure `node-23` tool exists in Jenkins. Otherwise, rename the tool or install NodeJS plugin and create it.

---

## Next Steps

1. Create the five pipeline jobs in Jenkins.
2. Add the SSH credential (`hostpoint-ssh-key`).
3. Test **orderflow-backend-deploy-local** first.
4. Test **orderflow-backend-deploy-prod**.
5. Once stable, use **orderflow-backend-deploy-local-then-prod** as your main release job.
6. When frontend repo is ready, enable **orderflow-frontend-build** and **orderflow-frontend-deploy-prod**.
