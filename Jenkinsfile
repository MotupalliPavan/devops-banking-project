pipeline {

    agent any

    environment {
    DOCKERHUB_REPO = "motupallipavan/digital-banking-cicd-app"
    IMAGE_TAG = "${BUILD_NUMBER}"
    APP_IMAGE = "motupallipavan/digital-banking-cicd-app:${BUILD_NUMBER}"
    LAST_SUCCESS_FILE = "last-successful-image.txt"
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
                sh 'java -version'
                sh 'mvn -version'
                sh 'docker --version'
                sh 'docker compose version'
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
        withSonarQubeEnv('SonarQube') {
            sh '''
                mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar \
                  -Dsonar.projectKey=digital-banking-system \
                  -Dsonar.projectName=digital-banking-system
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
            echo "Building Docker image: ${APP_IMAGE}"

            docker build -t ${APP_IMAGE} .
        '''
    }
}
	stage('Docker Push') {
    steps {
        withCredentials([
            usernamePassword(
                credentialsId: 'dockerhub-credentials',
                usernameVariable: 'DOCKER_USERNAME',
                passwordVariable: 'DOCKER_PASSWORD'
            )
        ]) {
            sh '''
                echo "$DOCKER_PASSWORD" | docker login \
                    -u "$DOCKER_USERNAME" \
                    --password-stdin

                echo "Pushing Docker image: ${APP_IMAGE}"

                docker push ${APP_IMAGE}
            '''
        }
    }
}
	stage('Deploy') {
	steps {
        sh '''
            echo "Stopping existing deployment..."

            docker compose down --remove-orphans || true

            echo "Pulling image: ${APP_IMAGE}"

            IMAGE_TAG=${IMAGE_TAG} docker compose pull app

            echo "Starting application..."

            IMAGE_TAG=${IMAGE_TAG} docker compose up -d
        '''
    }
}
 	stage('Health Check') {
   	 steps {
        	sh '''
            	echo "Waiting for application to become healthy..."

            for i in $(seq 1 30); do

                STATUS=$(docker inspect \
                    --format='{{.State.Health.Status}}' \
                    digital-banking-app 2>/dev/null || echo "starting")

                echo "Application health: $STATUS"

                if [ "$STATUS" = "healthy" ]; then

                    echo "Application is healthy!"

                    curl --fail --silent --show-error \
                        http://localhost:8081/login > /dev/null

                    exit 0
                fi

                if [ "$STATUS" = "unhealthy" ]; then
                    echo "Application became unhealthy."
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
	stage('Record Successful Deployment') {
    steps {
        script {
            writeFile(
                file: env.LAST_SUCCESS_FILE,
                text: env.IMAGE_TAG
            )

            archiveArtifacts(
                artifacts: env.LAST_SUCCESS_FILE,
                fingerprint: true
            )

            echo "Successfully deployed image: ${env.APP_IMAGE}"
        }
    }
}
	stage('Docker Cleanup') {
            steps {
                sh '''
                    echo "Cleaning unused Docker resources..."

                    docker image prune -f
                    docker container prune -f
                '''
            }
        }
    }

    post {

        success {
            echo 'Banking application deployed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the console output.'
        }

        always {
            sh 'docker compose ps || true'
        }
    }
}

	stage('Verify Environment') {
    		steps {
        	sh '''
            	echo "BUILD_NUMBER=${BUILD_NUMBER}"
            	echo "DOCKERHUB_REPO=${DOCKERHUB_REPO}"
            	echo "IMAGE_TAG=${IMAGE_TAG}"
            	echo "APP_IMAGE=${APP_IMAGE}"
        	'''
    }
}
