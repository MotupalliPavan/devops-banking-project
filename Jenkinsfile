pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        APP_NAME       = 'digital-banking-cicd-app'
        IMAGE_TAG      = "${BUILD_NUMBER}"
        DOCKER_IMAGE   = "motupallipavan/digital-banking-cicd-app:${BUILD_NUMBER}"
        SONARQUBE      = 'SonarQube'
        DOCKER_CREDS   = 'dockerhub-credentials'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Environment') {
            steps {
                sh '''
                    java -version
                    mvn -version
                    docker --version
                    docker compose version
                '''
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit(
                        testResults: '**/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONARQUBE}") {
                    sh '''
                        mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    echo "Building Docker image: ${DOCKER_IMAGE}"
                    docker build -t ${DOCKER_IMAGE} .
                '''
            }
        }

        stage('Docker Login & Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: "${DOCKER_CREDS}",
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

                        docker push ${DOCKER_IMAGE}

                        docker tag ${DOCKER_IMAGE} motupallipavan/digital-banking-cicd-app:latest
                        docker push motupallipavan/digital-banking-cicd-app:latest

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    echo "Stopping existing Digital Banking containers..."

                    docker compose down --remove-orphans || true

                    echo "Starting Digital Banking deployment..."

                    IMAGE_TAG=${IMAGE_TAG} docker compose up -d
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "Waiting for Digital Banking application..."

                    for i in $(seq 1 30); do

                        STATUS=$(docker inspect \
                            --format='{{.State.Health.Status}}' \
                            digital-banking-app \
                            2>/dev/null || echo "starting")

                        echo "Application health: $STATUS"

                        if [ "$STATUS" = "healthy" ]; then
                            echo "Digital Banking application is healthy!"

                            curl --fail --silent --show-error \
                                http://localhost:8081/login > /dev/null

                            exit 0
                        fi

                        if [ "$STATUS" = "unhealthy" ]; then
                            echo "Application is unhealthy."
                            docker compose logs app
                            exit 1
                        fi

                        sleep 5
                    done

                    echo "Application did not become healthy within 150 seconds."

                    docker compose logs app

                    exit 1
                '''
            }
        }
    }

    post {
        success {
            echo 'Digital Banking application deployed successfully!'
        }

        failure {
            echo 'Digital Banking pipeline failed. Check the console logs.'
        }

        always {
            sh 'docker compose ps || true'
        }
    }
}
