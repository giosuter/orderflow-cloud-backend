pipeline {
  agent any
  options {
    ansiColor('xterm')
    timestamps()
  }
  parameters {
    string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to build')
  }
  environment {
    REPO_URL = 'https://github.com/giosuter/orderflow-cloud-backend.git'
    JDK_TOOL = 'jdk-21' // Jenkins Global Tool name for JDK 21
    MAVEN_OPTS = '-Xms256m -Xmx1024m'
    WAR_NAME   = 'orderflow-api.war'
    CONTEXT_PATH = 'orderflow-api'

    // Remote Hostpoint
    REMOTE_HOST = 'zitatusi.myhostpoint.ch'
    REMOTE_USER = 'zitatusi'
    REMOTE_TOMCAT_DIR = '/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33'
    REMOTE_WEBAPPS    = "${REMOTE_TOMCAT_DIR}/webapps"
    // Jenkins credential id containing the SSH private key for REMOTE_USER@REMOTE_HOST
    SSH_CRED_ID = 'hostpoint-ssh-key'
  }
  tools {
    jdk "${JDK_TOOL}"
  }
  stages {
    stage('Checkout') {
      steps {
        git branch: "${params.BRANCH}", url: "${REPO_URL}"
      }
    }
    stage('Build & Test') {
      steps {
        sh 'mvn -v'
        sh 'mvn -B clean verify'
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
          junit 'target/failsafe-reports/*.xml'
          archiveArtifacts artifacts: 'target/*.war', fingerprint: true
        }
      }
    }
    stage('Deploy to Production') {
      steps {
        sshagent (credentials: [env.SSH_CRED_ID]) {
          sh '''
            set -e
            echo "Stopping remote Tomcat..."
            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "${REMOTE_TOMCAT_DIR}/bin/shutdown.sh || true"
            sleep 5 || true

            echo "Removing previous exploded app (if any)..."
            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "rm -rf ${REMOTE_WEBAPPS}/${CONTEXT_PATH}" || true

            echo "Uploading WAR..."
            scp -o StrictHostKeyChecking=no target/${WAR_NAME} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_WEBAPPS}/

            echo "Starting remote Tomcat with prod profile..."
            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "SPRING_PROFILES_ACTIVE=prod ${REMOTE_TOMCAT_DIR}/bin/startup.sh"

            echo "Waiting for app to boot..."
            sleep 15

            echo "Smoke test (remote):"
            curl -i --max-time 10 "https://devprojects.ch/${CONTEXT_PATH}/api/ping" || true
          '''
        }
      }
    }
  }
  post {
    success { echo 'Production deploy completed.' }
    failure { echo 'Production deploy failed. Check logs.' }
  }
}
