pipeline {
  agent any
  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build') {
      steps {
        sh 'mvn clean compile -DskipTests'
      }
    }

    stage('Test') {
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }

      }
      steps {
        sh 'mvn test'
      }
    }

    stage('Package') {
      post {
        success {
          archiveArtifacts(artifacts: 'target/*.jar', fingerprint: true)
        }

      }
      steps {
        sh 'mvn package -DskipTests'
      }
    }

  }
  tools {
    maven 'Maven'
    jdk 'JDK17'
  }
  environment {
    APP_NAME = 'cicd-sample-maven-project'
  }
  post {
    always {
      cleanWs()
    }

    success {
      echo 'Build and tests completed successfully!'
    }

    failure {
      echo 'Build or tests failed!'
    }

  }
}