pipeline {
  agent any

  tools {
    maven 'Maven'
    jdk   'jdk-21'
  }

  options {
    timestamps()
    ansiColor('xterm')
    buildDiscarder(logRotator(numToKeepStr: '20'))
    disableConcurrentBuilds()
    wrap([$class: 'AnsiColorBuildWrapper', colorMapName: 'xterm'])
  }

  triggers {
    githubPush()
  }

  environment {
    GIT_CREDENTIALS_ID = 'github-giosuter' // keep if your repo needs auth
  }

  stages {
    stage('Checkout') {
      steps {
        // Use one of the two lines below:
        // checkout scm
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
        sh 'mvn -B -U -DskipTests=false clean verify'
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml, **/target/failsafe-reports/*.xml'
          script {
            try {
              jacoco execPattern: 'target/jacoco.exec',
                     classPattern: 'target/classes',
                     sourcePattern: 'src/main/java'
            } catch (ignored) {
              echo 'No JaCoCo exec file found.'
            }
          }
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

    stage('Quality Gate (soft)') {
      steps {
        echo 'Add a hard coverage threshold once domain tests are in (e.g., fail if < 75%).'
      }
    }
  }

  post {
    success  { echo 'Build green.' }
    unstable { echo 'Unstable: check tests/coverage.' }
    failure  { echo 'Build failed.' }
  }
}