// File: Jenkinsfile  (orderflow-cloud-backend)
// Purpose: Build + test + copy WAR to Hostpoint (with H2 prod config)

pipeline {
  agent any

  tools {
    maven 'Maven'
    jdk   'jdk-21'
  }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    disableConcurrentBuilds()
  }

  triggers {
    githubPush()
  }

  environment {
    // GitHub credentials (already configured in your Jenkins)
    GIT_CREDENTIALS_ID = 'github-giosuter'

    // Hostpoint deploy settings
    HOSTPOINT_USER = 'zitatusi'
    HOSTPOINT_HOST = 'zitatusi.myhostpoint.ch'
    REMOTE_TOMCAT_DIR = '/home/zitatusi/app/tools/tomcat/apache-tomcat-10.1.33'

    // Name of the built WAR (from pom.xml: <finalName>orderflow-api</finalName>)
    WAR_NAME = 'orderflow-api.war'
  }

  stages {

    stage('Marker') {
      steps {
        echo 'JENKINSFILE_MARKER: orderflow-v1-build-and-copy'
      }
    }

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
        sh 'java -version || true'
        sh 'mvn -version || true'
      }
    }

    stage('Build & Test') {
      steps {
        sh 'mvn -B -U clean verify'
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
          archiveArtifacts artifacts: 'target/*.war, target/site/**', fingerprint: true, onlyIfSuccessful: false
        }
      }
    }

    stage('Deploy WAR to Hostpoint (copy only)') {
      when {
        branch 'main'
      }
      steps {
        sh '''
          echo "Checking for built WAR..."
          ls -l target || true

          if [ ! -f "target/${WAR_NAME}" ]; then
            echo "ERROR: target/${WAR_NAME} not found. Build may have failed."
            exit 1
          fi

          echo "Copying WAR to Hostpoint..."
          scp -o StrictHostKeyChecking=no "target/${WAR_NAME}" \
            "${HOSTPOINT_USER}@${HOSTPOINT_HOST}:${REMOTE_TOMCAT_DIR}/webapps/${WAR_NAME}"

          echo "WAR copied to Hostpoint:"
          echo "  ${REMOTE_TOMCAT_DIR}/webapps/${WAR_NAME}"
          echo "Bitte Tomcat auf Hostpoint manuell per supervisorctl neu starten."
        '''
      }
    }
  }

  post {
    success  { echo 'OrderFlow build + copy to Hostpoint: SUCCESS' }
    unstable { echo 'OrderFlow pipeline UNSTABLE – bitte Tests/Coverage prüfen.' }
    failure  { echo 'OrderFlow pipeline FAILED – bitte Jenkins-Logs ansehen.' }
  }
}