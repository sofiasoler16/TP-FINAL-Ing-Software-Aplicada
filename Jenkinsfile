pipeline {
    agent any

    environment {
        // Credenciales de DockerHub (usuario sofiasoler16044 + PAT)
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-sofia')
        IMAGE_NAME = "sofiasoler16044/gym-tracker"
    }

    stages {

        stage('Build Backend') {
            steps {
                dir('gym-tracker') {
                    sh 'chmod +x mvnw' // por las dudas
                    sh './mvnw -ntp -DskipTests clean package'
                }
            }
        }

        stage('Build Docker image') {
            steps {
                dir('gym-tracker') {
                    sh '''
                        docker build -t $IMAGE_NAME:latest .
                    '''
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                sh '''
                    echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin
                    docker push $IMAGE_NAME:latest
                '''
            }
        }
    }

    post {
        success {
            echo "Build and push successful!!"
        }
        failure {
            echo "Build failed"
        }
    }
}
