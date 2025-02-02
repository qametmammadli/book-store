pipeline {
    agent any
    environment {
        NEXUS_URL = '172.31.30.142:8081'
        NEXUS_REPO = 'book_store_repo'
        AWS_REGION = "us-east-1"
        aws_creds = 'aws_creds_book_store'
        registryCredential = 'ecr:us-east-1:aws_creds_book_store'
        imageName = "058006845506.dkr.ecr.us-east-1.amazonaws.com/book_store_image"
        bookStoreRegistry = "https://058006845506.dkr.ecr.us-east-1.amazonaws.com"
        cluster = "book_store"
        service = "book_store_svc"
    }
    tools {
        maven "MAVEN3.8"
        jdk "JDK17"
    }

    stages {
        stage('FetchCode') {
            steps {
                git branch: 'development', url: 'https://github.com/qametmammadli/book-store.git' 
            }
        }

        stage('UnitTest') {
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


        stage('SonarQubeAnalysis') {
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

        stage("QualityGate") {
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

        stage('BuildImage') {
            steps {
                script {
                    dockerImage = docker.build("${imageName}:${BUILD_NUMBER}", "--no-cache --file Dockerfile .")
                }
            }
        }

        stage('UploadToAWS') {
          steps {
            script {
              docker.withRegistry(bookStoreRegistry, registryCredential) {
                dockerImage.push("$BUILD_NUMBER")
                dockerImage.push('latest')
              }
            }
          }
        }

        stage('RemoveContainerImages') {
            steps {
                script {
                    sh "docker rmi -f ${imageName}:${BUILD_NUMBER} || true"
                    sh "docker rmi -f ${imageName}:latest || true"
                }
            }
        }

        stage('DeployToECS') {
            steps {
                withAWS(credentials: "${aws_creds}", region: "${AWS_REGION}") {
                    sh """
                    aws ecs update-service --cluster "$cluster" --service "$service" --force-new-deployment"
                    """
                }
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