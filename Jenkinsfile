pipeline {
  agent any

  tools {
    jdk 'jdk-21'
    maven 'maven-3.9'
  }

  options {
    timestamps()
    ansiColor('xterm')
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  environment {
    GIT_CREDENTIALS_ID = 'github-giosuter'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout([
          $class: 'GitSCM',
          branches: [[name: '*/main']],
          userRemoteConfigs: [[
            url: 'https://github.com/giosuter/orderflow-cloud-backend.git',
            credentialsId: env.GIT_CREDENTIALS_ID
          ]]
        ])
      }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn -B clean verify'
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
          publishHTML(target: [
            reportDir: 'target/site/jacoco',
            reportFiles: 'index.html',
            reportName: 'JaCoCo Coverage',
            keepAll: true,
            alwaysLinkToLastBuild: true
          ])
          archiveArtifacts artifacts: 'target/*.war, target/site/**', fingerprint: true, onlyIfSuccessful: false
        }
      }
    }
  }

  post {
    success { echo '✅ Build green. Ping endpoint, Actuator & Swagger should be OK.' }
    unstable { echo '⚠️ Unstable: check tests and coverage.' }
    failure { echo '❌ Build failed.' }
  }
}