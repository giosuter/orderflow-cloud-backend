# OrderFlow — Jenkins Pipeline (Production Deploy)

This guide shows you how to create a **Jenkinsfile** and a **Jenkins pipeline job** that builds the OrderFlow API (Spring Boot WAR) and deploys it to **production** on Hostpoint (Tomcat 10.1.33).

> Target app: `orderflow-api.war` → Tomcat context `/orderflow-api`  
> Jenkins URL (local): `http://localhost:9090`  
> Remote host: `zitatusi@zitatusi.myhostpoint.ch`

---

## 0) Prerequisites (once)

1. **JDK & Maven in Jenkins**
   - **Manage Jenkins → Tools**
   - Add **JDK 21** (e.g., name: `jdk21`) – do **not** install automatically if JDK already on PATH.
   - Add **Maven** (e.g., name: `maven-3.9`) and select a 3.9.x version.

2. **SSH credentials (private key)**
   - **Manage Jenkins → Credentials → (global) → Add Credentials**
   - Kind: **SSH Username with private key**
   - ID: `hostpoint_ssh`
   - Username: `zitatusi`
   - Private Key: paste your private key (the one you use for `ssh zitatusi@zitatusi.myhostpoint.ch`).
   - Description: `Hostpoint SSH (zitatusi)`

3. **Agent/Node labels (optional)**
   - If you run everything on the Jenkins master, you can omit labels.
   - If you use a dedicated node, create a label (e.g., `macos`) and use it in the pipeline.

4. **Verify remote Tomcat paths**
   ```bash
   # On Hostpoint (already used successfully):
   REMOTE_TOMCAT_DIR="/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33"
   REMOTE_WEBAPPS="$REMOTE_TOMCAT_DIR/webapps"
   REMOTE_BIN="$REMOTE_TOMCAT_DIR/bin"
   ```

---

## 1) Add the Jenkinsfile to your repo

Create **`Jenkinsfile.prod`** at the root of your backend repository:
`/Users/giovannisuter/dev/projects/orderflow-cloud/back-end/orderflow-cloud-backend/Jenkinsfile.prod`

```groovy
pipeline {
  agent any

  tools {
    jdk 'jdk21'
    maven 'maven-3.9'
  }

  environment {
    // ---- Git ----
    GIT_REPO = 'https://github.com/giosuter/orderflow-cloud-backend.git'
    GIT_BRANCH = 'main'

    // ---- Build output ----
    WAR_NAME = 'orderflow-api.war'
    WAR_PATH = "target/${WAR_NAME}"

    // ---- Remote host ----
    PROD_HOST = 'zitatusi.myhostpoint.ch'
    PROD_USER = 'zitatusi'
    SSH_CRED_ID = 'hostpoint_ssh'

    // ---- Remote Tomcat ----
    REMOTE_TOMCAT_DIR = '/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33'
    REMOTE_WEBAPPS   = "${REMOTE_TOMCAT_DIR}/webapps"
    REMOTE_BIN       = "${REMOTE_TOMCAT_DIR}/bin"

    // Use prod profile when Tomcat starts (Catalina uses this env)
    CATALINA_OPTS = '-Xms512m -Xmx1024m -Dspring.profiles.active=prod'
  }

  options {
    timestamps()
    ansiColor('xterm')
  }

  stages {
    stage('Checkout') {
      steps {
        git branch: "${GIT_BRANCH}", url: "${GIT_REPO}"
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -v'
        sh 'mvn -B -U clean verify'  // runs unit + IT tests
      }
    }

    stage('Package') {
      steps {
        sh 'mvn -B -DskipTests package'
        sh 'ls -lh target || true'
        sh 'test -f "${WAR_PATH}"'
      }
    }

    stage('Stop Tomcat') {
      steps {
        sshagent(credentials: [env.SSH_CRED_ID]) {
          sh '''
          set -e
          ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} "ps -ef | grep -i 'org.apache.catalina.startup.Bootstrap' | grep -v grep >/dev/null && ${REMOTE_BIN}/shutdown.sh || true"
          # Give Tomcat time to stop
          ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} "sleep 5"
          '''
        }
      }
    }

    stage('Deploy WAR') {
      steps {
        sshagent(credentials: [env.SSH_CRED_ID]) {
          sh '''
          set -e
          # Remove exploded folder to avoid stale classes
          ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} "rm -rf ${REMOTE_WEBAPPS}/orderflow-api"
          # Copy WAR
          scp -o StrictHostKeyChecking=no "${WAR_PATH}" ${PROD_USER}@${PROD_HOST}:${REMOTE_WEBAPPS}/
          # Ensure correct name
          ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} "ls -l ${REMOTE_WEBAPPS}/${WAR_NAME}"
          '''
        }
      }
    }

    stage('Start Tomcat') {
      steps {
        sshagent(credentials: [env.SSH_CRED_ID]) {
          sh '''
          set -e
          ssh -o StrictHostKeyChecking=no ${PROD_USER}@${PROD_HOST} "export CATALINA_OPTS=\"${CATALINA_OPTS}\" && ${REMOTE_BIN}/startup.sh"
          # Give Tomcat time to deploy and start the context
          sleep 10
          '''
        }
      }
    }

    stage('Smoke Test') {
      steps {
        // Public host goes through NGINX proxy
        sh '''
        set -e
        echo "PING:"
        curl -fsS https://devprojects.ch/orderflow-api/api/ping || (echo "PING failed" && exit 1)
        echo
        echo "OpenAPI:"
        curl -fsS https://devprojects.ch/orderflow-api/v3/api-docs | head -c 200 || (echo "OpenAPI failed" && exit 1)
        echo
        '''
      }
    }
  }

  post {
    success {
      echo 'Production deployment succeeded.'
    }
    failure {
      echo 'Production deployment failed.'
    }
    always {
      archiveArtifacts artifacts: 'target/*.war', fingerprint: true, onlyIfSuccessful: false
      junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
    }
  }
}
```

> Notes  
> - Uses `sshagent` with the credential ID `hostpoint_ssh`.  
> - Restarts Tomcat cleanly, uploads `orderflow-api.war`, waits, and then smoke-tests two URLs.  
> - Ensures `SPRING_PROFILES_ACTIVE=prod` via `CATALINA_OPTS` in the startup stage.

Commit it:
```bash
cd ~/dev/projects/orderflow-cloud/back-end/orderflow-cloud-backend
git add Jenkinsfile.prod
git commit -m "Add Jenkinsfile for production deploy (Jenkinsfile.prod)"
git push
```

---

## 2) Create the Jenkins Pipeline Job (Production)

1. Open **Jenkins → New Item**  
2. Name: **`orderflow-deploy-prod`**  
3. Type: **Pipeline** → OK  
4. **General**: (optional) Add a description: “Build & deploy OrderFlow API to production (Hostpoint Tomcat).”  
5. **Pipeline** section:  
   - **Definition**: *Pipeline script from SCM*  
   - **SCM**: *Git*  
   - **Repository URL**: `https://github.com/giosuter/orderflow-cloud-backend.git`  
   - **Credentials**: your GitHub credential (if private repo; otherwise leave blank)  
   - **Branch Specifier**: `*/main`  
   - **Script Path**: `Jenkinsfile.prod`  
6. **Save**.

Run the job: **Build Now**.

---

## 3) Verify deployment

After the job finishes successfully:

- Ping
  ```bash
  curl -i https://devprojects.ch/orderflow-api/api/ping
  ```

- OpenAPI docs (JSON)
  ```bash
  curl -i https://devprojects.ch/orderflow-api/v3/api-docs
  ```

- Swagger UI (HTML page)
  ```
  https://devprojects.ch/orderflow-api/swagger-ui/index.html
  ```

If the ping returns 200 and Swagger UI loads, your production deployment is good.

---

## 4) Rollback (manual quick fix)

If the new WAR is broken:

```bash
# From your laptop (using your usual SSH key)
ssh zitatusi@zitatusi.myhostpoint.ch <<'EOF'
  REMOTE_TOMCAT_DIR="/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33"
  REMOTE_WEBAPPS="$REMOTE_TOMCAT_DIR/webapps"
  $REMOTE_TOMCAT_DIR/bin/shutdown.sh || true
  # Restore previous WAR (assuming you kept a backup e.g. orderflow-api.prev.war)
  cp -f $REMOTE_WEBAPPS/orderflow-api.prev.war $REMOTE_WEBAPPS/orderflow-api.war
  rm -rf $REMOTE_WEBAPPS/orderflow-api
  CATALINA_OPTS='-Xms512m -Xmx1024m -Dspring.profiles.active=prod' $REMOTE_TOMCAT_DIR/bin/startup.sh
EOF
```

> Tip: Extend the Jenkinsfile to automatically keep a timestamped backup of the previous WAR before copying a new one (e.g., `mv orderflow-api.war orderflow-api.$(date +%F-%H%M%S).bak`).

---

## 5) Troubleshooting

- **404 for `/orderflow-api/api/ping`**  
  Check that the Tomcat exploded folder is `orderflow-api` and that the **context path** matches. Ensure the WAR is named `orderflow-api.war` and Tomcat deployed it successfully.

- **White Swagger page or 500 on `/v3/api-docs`**  
  Check SpringDoc version alignment with Spring Boot (you’re on Spring Boot 3.5.x and SpringDoc 2.8.6 which is good). Review `catalina.out` for stack traces.

- **Flyway/H2 issues**  
  In production you’re currently using H2. Avoid legacy URL flags not supported by H2 2.x; prefer a simple file URL (e.g., `jdbc:h2:file:~/.orderflow/data` with minimal extras).

- **Permissions**  
  If `scp` fails with “Permission denied”, ensure the SSH credential is correct and that the remote user can write to `${REMOTE_WEBAPPS}`.

---

## 6) Optional hardening

- Add a **parameterized build** (e.g., `SKIP_TESTS`, `WAIT_AFTER_STARTUP_SEC`).  
- Add a **health check loop** (retry `curl` for up to N seconds until 200).  
- Add **post-deploy smoke tests** for CRUD endpoints (create → get → delete).  
- Integrate with **orderflow-smoke-prod** job for a separate test suite.

---

### Done

You now have:
- A committed **Jenkinsfile.prod**
- A Jenkins pipeline **orderflow-deploy-prod** that builds & deploys to Hostpoint
- Curl commands and URLs to verify the live app
