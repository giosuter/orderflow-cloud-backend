pipeline {
  agent any
  tools {
    jdk 'JDK21'     // or remove if PATH already has Java 21
    maven 'Maven3'  // configure in Manage Jenkins → Tools
  }
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Build & Test') {
      steps { sh 'mvn -B clean verify' }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
          archiveArtifacts artifacts: 'target/site/jacoco/**', fingerprint: true
        }
      }
    }
  }
  post { always { echo 'Phase 1 finished.' } }
}