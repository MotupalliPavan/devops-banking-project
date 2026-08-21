pipeline {
    agent any

    environment {
        APP_NAME = "devops-banking-project-app"
        COMPOSE_FILE = "docker-compose.yml"
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }



onment') {
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
                sh 'docker compose up -d'
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "Waiting for application..."
                    sleep 10

            }
        }
    }
            echo 'Banking application deployed successfully!'
        }
        }

        always {
            sh 'docker compose ps || true'
        }
    }
}
        failure {
            echo 'Pipeline failed. Check the stage logs.'

    post {
        success {

                    echo "Application 

		    

