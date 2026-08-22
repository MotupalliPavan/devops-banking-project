pipeline {
    agent any

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
                	mvn sonar:sonar \
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
                sh 'docker compose build'
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    set -e

                    echo "=== Stopping existing banking deployment ==="
                    docker compose down --remove-orphans || true

                    echo "=== Starting new banking deployment ==="
                    docker compose up -d

                    echo "=== Deployment status ==="
                    docker compose ps
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
