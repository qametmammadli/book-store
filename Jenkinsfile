pipeline {
    agent any
    tools {
        maven "MAVEN3.8"
        jdk "JDK17"
    }

    stages {
        stage('Fetch Code') {
            steps {
                git branch: 'master', url: 'https://github.com/qametmammadli/book-store.git' 
            }
        }

        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn install -DskipTests'
            }

            post {
                success {
                    echo "Archive artifact"
                    archiveArtifacts '**/book_store-0.0.1-SNAPSHOT.jar'
                }
            }
        }
    }
}