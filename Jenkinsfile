pipeline {
    agent any

    tools {
        maven 'maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build JAR File') {
            steps {
                dir('KartingRM/KartingRM') {
                    bat 'mvn clean install'
                }
            }
        }

        stage('Tests') {
            steps {
                dir('KartingRM/KartingRM') {
                    bat 'mvn test'
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                dir('KartingRM/KartingRM') {
                    script {

                        bat 'docker context use default'
                        
                        withDockerRegistry([credentialsId: 'docker-credentials']) {
                            bat 'docker build -t ignacioavila23/kartingrm:latest .'
                            bat 'docker push ignacioavila23/kartingrm:latest'
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check the logs above.'
        }
    }
}
