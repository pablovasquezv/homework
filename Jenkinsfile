pipeline {
    agent {
        node {
            label 'executor'
        }
    }
    environment {
        VAR = "VARS"
    }
    options {
        timestamps ()   
        timeout(time: 10, unit: 'MINUTES') 
    }
    stages {
        stage('Build') {
            steps {
                script {
                    echo "Building 'dist and jar' and Docker image"
                    sh "docker-compose build"
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    // Comando docker o con docker-compose
                    echo "Deploy"
                    sh "docker-compose up -d"
                }
            }
        }
    }
    // post {
    //     always {
    //         //Cleanup Workspace
    //         step([$class: 'WsCleanup'])
    //     }
    // }
}