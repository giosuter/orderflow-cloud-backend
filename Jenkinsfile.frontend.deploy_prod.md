pipeline {
  agent any
  options { ansiColor('xterm'); timestamps() }
  parameters {
    string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to deploy')
  }
  environment {
    REPO_URL = 'https://github.com/giosuter/orderflow-cloud-frontend.git'
    ANGULAR_PROJECT = 'orderflow-frontend'
    DIST_DIR = "dist/${ANGULAR_PROJECT}/browser"

    NODE_TOOL = 'node-23'

    // Remote Hostpoint (static web dir for the SPA)
    REMOTE_HOST = 'zitatusi.myhostpoint.ch'
    REMOTE_USER = 'zitatusi'
    REMOTE_WEBROOT = '/home/zitatusi/www/devprojects.ch/orderflow-cloud'  // adjust if you prefer /orderflow/
    SSH_CRED_ID = 'hostpoint-ssh-key'
  }
  tools { nodejs "${NODE_TOOL}" }
  stages {
    stage('Checkout') {
      steps { git branch: "${params.BRANCH}", url: "${REPO_URL}" }
    }
    stage('Install & Build') {
      steps {
        sh 'node -v && npm -v'
        sh 'npm ci'
        sh 'npm run build --if-present || npx ng build --configuration production'
      }
    }
    stage('Deploy to Production (static)') {
      steps {
        sshagent (credentials: [env.SSH_CRED_ID]) {
          sh '''
            set -e
            echo "Creating remote dir..."
            ssh -o StrictHostKeyChecking=no ${REMOTE_USER}@${REMOTE_HOST} "mkdir -p ${REMOTE_WEBROOT}"
            echo "Uploading build..."
            rsync -avz --delete "${DIST_DIR}/" ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_WEBROOT}/
            echo "Deployed to https://devprojects.ch/orderflow-cloud/"
          '''
        }
      }
    }
  }
  post {
    success { echo 'Frontend deploy OK.' }
    failure { echo 'Frontend deploy failed.' }
  }
}
