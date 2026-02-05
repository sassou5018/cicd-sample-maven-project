pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }

    tools {
        maven 'Maven'
        jdk 'JDK17'
    }

    triggers {
        pollSCM('* * * * *')  // Poll every minute (minimum interval)
        githubPush()
    }

    environment {
        APP_NAME = 'cicd-sample-maven-project'
        NEXUS_REGISTRY = 'localhost:8082'
        NEXUS_CREDENTIALS_ID = 'nexus-docker-credentials'
    }

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
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=project-cicd'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'mvn compile jib:dockerBuild -DskipTests'
            }
            post {
                success {
                    echo "Docker image built: ${NEXUS_REGISTRY}/${APP_NAME}:latest"
                }
            }
        }

        stage('Push to Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${NEXUS_CREDENTIALS_ID}", usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh '''
                        echo "Pushing to Nexus with user: $NEXUS_USER"
                        mvn compile jib:build -DskipTests -DsendCredentialsOverHttp=true \
                            -Djib.to.auth.username="$NEXUS_USER" \
                            -Djib.to.auth.password="$NEXUS_PASS"
                    '''
                }
            }
            post {
                success {
                    echo "Docker image pushed to Nexus: ${NEXUS_REGISTRY}/${APP_NAME}:latest"
                }
            }
        }
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