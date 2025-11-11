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
    // Local Tomcat on the Mac where Jenkins runs
    LOCAL_TOMCAT_DIR = '/Users/giovannisuter/dev/tools/apache-tomcat-10.1.33'
    LOCAL_WEBAPPS    = "${LOCAL_TOMCAT_DIR}/webapps"
    WAR_NAME         = 'orderflow-api.war'
    CONTEXT_PATH     = 'orderflow-api'
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
    stage('Build') {
      steps {
        sh 'mvn -v'
        sh 'mvn -B -DskipTests clean package'
      }
      post {
        always {
          archiveArtifacts artifacts: 'target/*.war', fingerprint: true
        }
      }
    }
    stage('Deploy to Local Tomcat') {
      steps {
        sh '''
          set -e
          echo "Stopping local Tomcat..."
          ${LOCAL_TOMCAT_DIR}/bin/shutdown.sh || true
          sleep 3 || true

          echo "Removing previous exploded app (if any)..."
          rm -rf "${LOCAL_WEBAPPS}/${CONTEXT_PATH}" || true

          echo "Copying WAR..."
          cp target/${WAR_NAME} "${LOCAL_WEBAPPS}/"

          echo "Starting local Tomcat..."
          SPRING_PROFILES_ACTIVE=prod ${LOCAL_TOMCAT_DIR}/bin/startup.sh

          echo "Waiting for app to boot..."
          sleep 12

          echo "Smoke test (local): curl http://127.0.0.1:8080/${CONTEXT_PATH}/api/ping"
          curl -i --max-time 10 "http://127.0.0.1:8080/${CONTEXT_PATH}/api/ping" || true
        '''
      }
    }
  }
  post {
    success { echo 'Local deploy completed.' }
    failure { echo 'Local deploy failed. Check logs.' }
  }
}
