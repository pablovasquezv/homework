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
        stage('Rollback') {
            steps {
                script {
                    echo "Rollback"
                    sh "docker-compose down"
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