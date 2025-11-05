pipeline {
  agent any

  tools {
    // Keep this name equal to your Jenkins Global Tool config
    maven 'Maven'        // <-- you said Name: Maven
    jdk   'jdk-21'       // <-- make sure you created a JDK tool with this name
  }

  options {
    timestamps()
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

    stage('Verify tool versions') {
      steps {
        sh 'java -version'
        sh 'mvn -version'
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
    success { echo '✅ Build green.' }
    unstable { echo '⚠️ Unstable: check tests/coverage.' }
    failure { echo '❌ Build failed.' }
  }
}