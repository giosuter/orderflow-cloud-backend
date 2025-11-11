
# Deployment Scripts for OrderFlow Cloud Backend

This document contains production‑ready deployment scripts for Hostpoint (Tomcat 10.1.33, WAR deployment) and local macOS development.

---

## 1. Local Deployment Script (macOS)

**Path:** `scripts/deploy_local.sh`  
**Purpose:** Build the backend, stop local Tomcat, deploy WAR, restart Tomcat.

```bash
#!/bin/bash
set -e

PROJECT_DIR="/Users/giovannisuter/dev/projects/orderflow-cloud/back-end/orderflow-cloud-backend"
TOMCAT_DIR="/Users/giovannisuter/dev/tools/apache-tomcat-10.1.33"
WEBAPPS="$TOMCAT_DIR/webapps"
WAR_NAME="orderflow-api.war"

echo "Building project..."
cd "$PROJECT_DIR"
mvn -DskipTests=true clean package

echo "Stopping Tomcat..."
"$TOMCAT_DIR/bin/shutdown.sh" || true
sleep 3

echo "Cleaning old deployment..."
rm -rf "$WEBAPPS/orderflow-api"
rm -f "$WEBAPPS/$WAR_NAME"

echo "Copying new WAR..."
cp "$PROJECT_DIR/target/$WAR_NAME" "$WEBAPPS/"

echo "Starting Tomcat..."
"$TOMCAT_DIR/bin/startup.sh"

echo "Local deployment done."
```

---

## 2. Production Deployment Script (Hostpoint)

**Path:** `scripts/deploy_prod.sh`  
**Purpose:** Build project locally, upload WAR via SCP, restart remote Tomcat via SSH.

⚠️ Hostpoint uses **FreeBSD**, custom Tomcat path inside `/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33`.

```bash
#!/bin/bash
set -e

PROJECT_DIR="/Users/giovannisuter/dev/projects/orderflow-cloud/back-end/orderflow-cloud-backend"
WAR_NAME="orderflow-api.war"

REMOTE_HOST="zitatusi@zitatusi.myhostpoint.ch"
REMOTE_TOMCAT="/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33"
REMOTE_WEBAPPS="$REMOTE_TOMCAT/webapps"

echo "Building project..."
cd "$PROJECT_DIR"
mvn -DskipTests=true clean package

echo "Copying WAR to server..."
scp "$PROJECT_DIR/target/$WAR_NAME" "$REMOTE_HOST:$REMOTE_WEBAPPS/"

echo "Stopping remote Tomcat..."
ssh "$REMOTE_HOST" "$REMOTE_TOMCAT/bin/shutdown.sh || true"
sleep 4

echo "Cleaning old deployment..."
ssh "$REMOTE_HOST" "rm -rf $REMOTE_WEBAPPS/orderflow-api; rm -f $REMOTE_WEBAPPS/$WAR_NAME"
sleep 2

echo "Uploading fresh WAR..."
scp "$PROJECT_DIR/target/$WAR_NAME" "$REMOTE_HOST:$REMOTE_WEBAPPS/"

echo "Starting remote Tomcat..."
ssh "$REMOTE_HOST" "$REMOTE_TOMCAT/bin/startup.sh"

echo "Deployment completed."
```

---

## 3. Jenkins Deployment Pipeline

**Jenkinsfile snippet for building + production deploy:**

```groovy
pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn -DskipTests=true clean package'
            }
        }
        stage('Deploy to Hostpoint') {
            steps {
                sh '''
                scp target/orderflow-api.war zitatusi@zitatusi.myhostpoint.ch:/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33/webapps/
                ssh zitatusi@zitatusi.myhostpoint.ch "/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33/bin/shutdown.sh || true"
                sleep 3
                ssh zitatusi@zitatusi.myhostpoint.ch "/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33/bin/startup.sh"
                '''
            }
        }
    }
}
```

---

## 4. Verify Deployment

### Local:
```
http://localhost:8080/orderflow-api/api/ping
http://localhost:8080/orderflow-api/swagger-ui.html
http://localhost:8080/orderflow-api/actuator/health
```

### Production:
```
https://devprojects.ch/orderflow-api/api/ping
https://devprojects.ch/orderflow-api/swagger-ui/index.html
https://devprojects.ch/orderflow-api/actuator/health
```

---

## 5. Notes
- Ensure permissions for `.sh` scripts:  
  `chmod +x scripts/*.sh`
- Hostpoint Tomcat sometimes requires **manual deletion** of exploded WAR folders.
- Spring profile for production uses `SPRING_PROFILES_ACTIVE=prod`.

---

End of Deployment Scripts.
