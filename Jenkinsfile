pipeline {
    agent any

    environment {
        DOCKERHUB_REPO = 'motupallipavan/online-banking-cicd-app'
        IMAGE_TAG = "${BUILD_NUMBER}"
        APP_IMAGE = "motupallipavan/online-banking-cicd-app:${BUILD_NUMBER}"
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
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
                    echo "===== ONLINE BANKING CI/CD ====="
                    echo "===== Environment ====="

                    java -version
                    mvn -version
                    docker --version
                    docker compose version

                    echo "BUILD_NUMBER=${BUILD_NUMBER}"
                    echo "DOCKERHUB_REPO=${DOCKERHUB_REPO}"
                    echo "IMAGE_TAG=${IMAGE_TAG}"
                    echo "APP_IMAGE=${APP_IMAGE}"
                '''
            }
        }

        stage('Compile') {
            steps {
                echo 'Compiling Online Banking application...'
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running Online Banking tests...'
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
                echo 'Running SonarQube analysis...'

                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo 'Waiting for SonarQube Quality Gate...'

                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging Online Banking application...'

                sh 'mvn package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Online Banking Docker image...'

                sh '''
                    echo "===== Docker Build ====="
                    echo "Building image: ${APP_IMAGE}"

                    docker build \
                        -t ${APP_IMAGE} \
                        .

                    echo "Docker image built successfully."

                    docker images | grep online-banking-cicd-app || true
                '''
            }
        }

        stage('Docker Tag') {
            steps {
                echo 'Creating Docker latest tag...'

                sh '''
                    echo "===== Docker Tag ====="

                    docker tag \
                        ${APP_IMAGE} \
                        ${DOCKERHUB_REPO}:latest

                    echo "Docker tags created successfully."

                    docker images | grep online-banking-cicd-app || true
                '''
            }
        }

        stage('Docker Hub Push') {
            steps {
                echo 'Pushing Online Banking images to Docker Hub...'

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    sh '''
                        echo "===== Docker Hub Login ====="

                        echo "$DOCKER_PASSWORD" | docker login \
                            --username "$DOCKER_USERNAME" \
                            --password-stdin

                        echo "===== Pushing Versioned Image ====="

                        docker push ${APP_IMAGE}

                        echo "===== Pushing Latest Image ====="

                        docker push ${DOCKERHUB_REPO}:latest

                        echo "Docker images pushed successfully."

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying Online Banking application...'

                sh '''
                    echo "===== ONLINE BANKING DEPLOYMENT ====="

                    echo "Stopping existing deployment..."

                    docker compose down --remove-orphans || true

                    echo "Pulling Online Banking image: ${IMAGE_TAG}"

                    IMAGE_TAG=${IMAGE_TAG} docker compose pull app

                    echo "Starting Online Banking application..."

                    IMAGE_TAG=${IMAGE_TAG} docker compose up -d

                    echo "Deployment started successfully."
                '''
            }
        }

        stage('Health Check') {
            steps {
                echo 'Checking Online Banking application health...'

                sh '''
                    echo "===== ONLINE BANKING HEALTH CHECK ====="

                    echo "Waiting for application to become healthy..."

                    for i in $(seq 1 30); do

                        STATUS=$(docker inspect \
                            --format='{{.State.Health.Status}}' \
                            online-banking-app 2>/dev/null || echo "starting")

                        echo "Attempt $i/30"
                        echo "Application health: $STATUS"

                        if [ "$STATUS" = "healthy" ]; then

                            echo "Online Banking container is healthy."

                            echo "Checking application endpoint..."

                            curl --fail \
                                 --silent \
                                 --show-error \
                                 http://localhost:8081/login \
                                 > /dev/null

                            echo "Online Banking application is responding."

                            echo "===== HEALTH CHECK PASSED ====="

                            exit 0
                        fi

                        if [ "$STATUS" = "unhealthy" ]; then

                            echo "Online Banking application became unhealthy."

                            echo "===== APPLICATION LOGS ====="

                            docker compose logs --tail=100 app

                            exit 1
                        fi

                        sleep 5
                    done

                    echo "Online Banking application did not become healthy within 150 seconds."

                    echo "===== APPLICATION LOGS ====="

                    docker compose logs --tail=100 app

                    exit 1
                '''
            }
        }
    }

    post {

        always {
            sh '''
                echo "===== ONLINE BANKING CONTAINERS ====="

                docker compose ps || true

                echo "===== ONLINE BANKING DOCKER IMAGES ====="

                docker images | grep online-banking-cicd-app || true
            '''
        }

        success {
            echo 'Online Banking application deployed successfully and health check passed!'
        }

        failure {
            echo 'Online Banking pipeline failed. Check the console output.'
        }
    }
}
