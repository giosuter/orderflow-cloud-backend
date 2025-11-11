pipeline {
  agent any
  options { ansiColor('xterm'); timestamps() }
  parameters {
    string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to build')
    booleanParam(name: 'DEPLOY_PROD', defaultValue: true, description: 'Deploy to production after local deploy passes')
  }
  environment {
    REPO_URL = 'https://github.com/giosuter/orderflow-cloud-backend.git'
    JDK_TOOL = 'jdk-21'
    MAVEN_OPTS = '-Xms256m -Xmx1024m'
    WAR_NAME   = 'orderflow-api.war'
    CONTEXT_PATH = 'orderflow-api'

    // Local Tomcat
    LOCAL_TOMCAT_DIR = '/Users/giovannisuter/dev/tools/apache-tomcat-10.1.33'
    LOCAL_WEBAPPS    = "${LOCAL_TOMCAT_DIR}/webapps"

    // Remote Hostpoint
    REMOTE_HOST = 'zitatusi.myhostpoint.ch'
    REMOTE_USER = 'zitatusi'
    REMOTE_TOMCAT_DIR = '/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33'
    REMOTE_WEBAPPS    = "${REMOTE_TOMCAT_DIR}/webapps"
    SSH_CRED_ID = 'hostpoint-ssh-key'
  }
  tools { jdk "${JDK_TOOL}" }
  stages {
    stage('Checkout') {
      steps { git branch: "${params.BRANCH}", url: "${REPO_URL}" }
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
    stage('Deploy to Local') {
      steps {
        sh '''
          set -e
          ${LOCAL_TOMCAT_DIR}/bin/shutdown.sh || true
          sleep 3 || true
          rm -rf "${LOCAL_WEBAPPS}/${CONTEXT_PATH}" || true
          cp target/${WAR_NAME} "${LOCAL_WEBAPPS}/"
          SPRING_PROFILES_ACTIVE=prod ${LOCAL_TOMCAT_DIR}/bin/startup.sh
          sleep 12
          curl -i --max-time 10 "http://127.0.0.1:8080/${CONTEXT_PATH}/api/ping" || true
        '''
      }
    }
    stage('Deploy to Production') {
      when { expression { return params.DEPLOY_PROD } }
      steps {
        sshagent (credentials: [env.SSH_CRED_ID]) {
          sh '''
            set -e
            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "${REMOTE_TOMCAT_DIR}/bin/shutdown.sh || true"
            sleep 5 || true
            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "rm -rf ${REMOTE_WEBAPPS}/${CONTEXT_PATH}" || true
            scp -o StrictHostKeyChecking=no target/${WAR_NAME} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_WEBAPPS}/
            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "SPRING_PROFILES_ACTIVE=prod ${REMOTE_TOMCAT_DIR}/bin/startup.sh"
            sleep 15
            curl -i --max-time 10 "https://devprojects.ch/${CONTEXT_PATH}/api/ping" || true
          '''
        }
      }
    }
  }
  post {
    success { echo 'Local then Prod deploy completed.' }
    failure { echo 'Pipeline failed. See logs.' }
  }
}
