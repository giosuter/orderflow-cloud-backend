pipeline {
  agent any
  options { ansiColor('xterm'); timestamps() }
  parameters {
    string(name: 'BRANCH', defaultValue: 'main', description: 'Git branch to build')
  }
  environment {
    // Adjust to your frontend repo URL (when you create it)
    REPO_URL = 'https://github.com/giosuter/orderflow-cloud-frontend.git'
    ANGULAR_PROJECT = 'orderflow-frontend'    // name used by Angular CLI in angular.json
    DIST_DIR = "dist/${ANGULAR_PROJECT}/browser"

    NODE_TOOL = 'node-23'   // Jenkins Global Tool (NodeJS plugin) for Node 23.x
  }
  tools {
    nodejs "${NODE_TOOL}"
  }
  stages {
    stage('Checkout') {
      steps { git branch: "${params.BRANCH}", url: "${REPO_URL}" }
    }
    stage('Install') {
      steps {
        sh 'node -v && npm -v'
        sh 'npm ci'
      }
    }
    stage('Build') {
      steps {
        sh 'npm run build --if-present || npx ng build --configuration production'
      }
      post {
        always {
          archiveArtifacts artifacts: "${DIST_DIR}/**/*", fingerprint: true, onlyIfSuccessful: true
        }
      }
    }
  }
  post {
    success { echo 'Frontend build OK.' }
    failure { echo 'Frontend build failed.' }
  }
}
