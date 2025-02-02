def COLOR_MAP = [
    'SUCCESS': 'good', 
    'FAILURE': 'danger',
]

pipeline {
    agent any
    environment {
        NEXUS_URL = '172.31.30.142:8081'
        NEXUS_REPO = 'book_store_repo'
    }
    tools {
        maven "MAVEN3.8"
        jdk "JDK17"
    }

    stages {
        stage('Fetch Code') {
            steps {
                git branch: 'development', url: 'https://github.com/qametmammadli/book-store.git' 
            }
        }

        stage('Unit Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Checkstyle') {
            steps {
                sh 'mvn checkstyle:checkstyle'
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


        stage('SonarQube analysis') {
            environment {
                scannerHome = tool 'sonar6.2'
            }
            steps {
              withSonarQubeEnv('sonarserver') {
                sh '''${scannerHome}/bin/sonar-scanner \
                    -Dsonar.projectKey=book_store \
                    -Dsonar.projectName=book_store \
                    -Dsonar.projectVersion=1.0 \
                    -Dsonar.sources=src/ \
                    -Dsonar.java.binaries=target/classes \
                    -Dsonar.junit.reportsPath=target/surefire-reports/ \
                    -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                    -Dsonar.checkstyle.reportPaths=target/checkstyle-result.xml'''
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

	     stage("UploadArtifact") {
            steps {
                nexusArtifactUploader(
                  nexusVersion: 'nexus3',
                  protocol: 'http',
                  nexusUrl: "${NEXUS_URL}",
                  groupId: 'com.qamet.user_publisher_book_store',
                  version: "${env.BUILD_ID}-${env.BUILD_TIMESTAMP}",
                  repository: "${NEXUS_REPO}",
                  credentialsId: 'NEXUS_LOGIN',
                  artifacts: [
                    [artifactId: 'book_store',
                     classifier: '',
                     file: 'target/book_store-0.0.1-SNAPSHOT.jar',
                     type: 'jar']
                  ]
                )
            }
        }

    }

    post {
        always {
            script {
                def buildStatus = currentBuild.currentResult ?: "UNKNOWN"
                def buildUser = currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)?.userId ?: "Automated Trigger"
                def buildDuration = currentBuild.durationString ?: "Unknown Duration"
                def errorLog = ""

                if (buildStatus == "FAILURE") {
                    try {
                        // Fetch last 20 lines of Jenkins Console Log (fixes the "file not found" error)
                        errorLog = currentBuild.rawBuild.getLog(20).join("\n")
                    } catch (Exception e) {
                        errorLog = "⚠ Unable to retrieve error logs."
                    }
                }

                def message = """
                *${buildStatus}:* Job *${env.JOB_NAME}* build *#${env.BUILD_NUMBER}*
                *Triggered by:* ${buildUser}
                *Duration:* ${buildDuration}
                *More info:* <${env.BUILD_URL}|View Full Logs>
                """

                if (buildStatus == "FAILURE" && errorLog) {
                    message += "\n\n🚨 *Error Log (Last 20 Lines):*\n```$errorLog```"
                }

                slackSend channel: '#ci-cd',
                    color: (buildStatus == "SUCCESS") ? 'good' : 'danger',
                    message: message
            }
        }
    }

}