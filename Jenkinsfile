pipeline {
    agent any
    environment {
        IMAGE_NAME = "c-compiler-app"
        IMAGE_TAG  = "${env.BUILD_NUMBER}"
        DOCKER_HOST = "unix:///var/run/docker.sock"
    }
    stages {
        stage('Checkout') {
            steps { checkout scm }
        }
        stage('Build with Maven') {
            steps { sh 'mvn clean package -DskipTests' }
        }
        stage('Test') {
            steps { sh 'mvn test' }
        }
        stage('Docker Build') {
            steps {
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -t ${IMAGE_NAME}:latest ."
            }
        }
        stage('Deploy to K8s') {
            steps {
                sh """
                  kubectl set image deployment/c-compiler-deployment \
                    c-compiler=${IMAGE_NAME}:${IMAGE_TAG} --record || \
                  kubectl apply -f k8s/deployment.yaml
                """
            }
        }
    }
    post {
        success { echo "Build ${IMAGE_TAG} deployed successfully." }
        failure { echo "Build failed — check console output." }
        always  { sh 'docker image prune -f' }
    }
}