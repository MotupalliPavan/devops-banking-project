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
            echo "Stopping existing banking deployment..."

            docker compose down --remove-orphans || true

            echo "Starting new banking deployment..."

            docker compose up -d
        '''
    }
}

        stage('Health Check') {
    steps {
        sh '''
            echo "Waiting for application to become healthy..."

            for i in $(seq 1 30); do
                STATUS=$(docker inspect --format='{{.State.Health.Status}}' digital-banking-cicd-app-1 2>/dev/null || echo "starting")

                echo "Application health: $STATUS"

                if [ "$STATUS" = "healthy" ]; then
                    echo "Application is healthy!"
                    curl --fail --silent --show-error http://localhost:8081/login > /dev/null
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

    post {
        success {
            echo 'Banking application deployed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the stage logs.'
        }

        always {
            sh 'docker compose ps || true'
        }
    }
}

