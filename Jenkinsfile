pipeline 
    agent any

    environment {
        APP_NAME = "digital-banking-cicd-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
        APP_IMAGE = "digital-banking-cicd-app:${BUILD_NUMBER}"
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
                	mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                  	-Dsonar.projectKey=digital-banking-system \
                  	-Dsonar.projectName="Digital Banking System"
            	'''
       			}	
   		 }
	}
	
	stage('Quality Gate') {
    		steps {
        	timeout(time: 5, unit: 'MINUTES') {
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
            echo "Building Docker image..."

            docker build \
                -t ${APP_IMAGE} \
                -t ${APP_NAME}:latest \
                .
        '''
            }
        }

        stage('Deploy') {
            steps {
                sh '''
            echo "Stopping existing deployment..."

            docker compose down --remove-orphans || true

            echo "Deploying image: ${APP_IMAGE}"

            export APP_IMAGE=${APP_IMAGE}

            docker compose up -d
        '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    set -e

                    echo "Waiting for application to become healthy..."

                    for i in $(seq 1 30); do
                        STATUS=$(docker compose ps -q app | xargs -r docker inspect --format='{{.State.Health.Status}}' 2>/dev/null || echo "starting")

                        echo "Application health: $STATUS"

                        if [ "$STATUS" = "healthy" ]; then
                            echo "Application is healthy!"

                            curl --fail --silent --show-error \
                                http://localhost:8081/login > /dev/null

                            echo "HTTP health check passed!"
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
