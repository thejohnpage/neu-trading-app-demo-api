pipeline {
  agent { label 'Spring-Agent' }
  options {
    timestamps()
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
    skipDefaultCheckout(true)
  }
  environment {
    APP_NAME = 'neu-trading-api'
    CONTAINER_NAME = 'trading-api'
    DOCKER_NETWORK = 'trading-network'
    API_PORT = '8081'
    MAVEN_IMAGE = 'maven:3.9.11-eclipse-temurin-21'
  }
  stages {
    stage('Checkout') {
      steps {
        checkout scm
        script {
          env.GIT_SHORT = sh(script: 'git rev-parse --short=8 HEAD', returnStdout: true).trim()
          env.BUILD_VERSION = "0.1.${BUILD_NUMBER}-${GIT_SHORT}"
        }
      }
    }
    stage('Unit Tests & Coverage') {
      steps {
        sh '''
          docker run --rm \
            -u "$(id -u):$(id -g)" \
            -v "$WORKSPACE:/workspace" \
            -v "$HOME/.m2:/tmp/.m2" \
            -e MAVEN_CONFIG=/tmp/.m2 \
            -w /workspace \
            $MAVEN_IMAGE mvn -B clean verify
        '''
      }
      post {
        always {
          junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
          archiveArtifacts allowEmptyArchive: true, artifacts: 'target/site/jacoco/**,target/jacoco.exec', fingerprint: true
        }
      }
    }
    stage('Build Docker Image') {
      steps {
        sh 'docker build -t $APP_NAME:$BUILD_VERSION -t $APP_NAME:ci-$BUILD_NUMBER .'
      }
    }
    stage('Deploy') {
      when { branch 'main' }
      steps {
        sh '''
          test -f .env || { echo "Missing deployment .env in Jenkins workspace"; exit 1; }
          docker rm -f $CONTAINER_NAME 2>/dev/null || true
          docker run -d --name $CONTAINER_NAME --restart unless-stopped \
            --network $DOCKER_NETWORK --env-file .env \
            -p $API_PORT:$API_PORT $APP_NAME:$BUILD_VERSION
        '''
      }
    }
    stage('Health Check') {
      when { branch 'main' }
      steps {
        sh '''
          for i in $(seq 1 30); do
            if curl -fsS http://localhost:$API_PORT/actuator/health | grep -q '"status":"UP"'; then
              echo "Trading API $BUILD_VERSION is UP"
              exit 0
            fi
            sleep 2
          done
          echo "Trading API failed health check"
          docker logs --tail 150 $CONTAINER_NAME || true
          exit 1
        '''
      }
    }
  }
  post {
    success { echo 'CI succeeded' }
    failure { echo 'CI failed' }
    cleanup { sh 'docker image prune -f >/dev/null 2>&1 || true' }
  }
}
