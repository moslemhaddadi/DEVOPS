// Jenkinsfile - Version Corrigée (Authentification SonarQube via env)
pipeline {
    agent any

    environment {
               SONARQUBE_URL = 'http://localhost:9000'
        PATH = "$PATH:/var/lib/jenkins/.local/bin"
        BUILD_TIMESTAMP = new Date().format("yyyy-MM-dd'T'HH:mm:ssXXX")
    }

    }

    stages {
        stage('1. Secrets Scan (Gitleaks )') {
            steps {
                script {
                    try {
                        sh "docker run --rm -v ${env.WORKSPACE}:/path zricethezav/gitleaks:latest detect --source=/path --verbose --exit-code 1 --report-path /path/gitleaks-report.json"
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("FAIL: Des secrets ont été détectés dans le code ! Consultez le rapport gitleaks-report.json.")
                    }
                }
            }
        }

        stage('2. SAST (SonarQube)') {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    // CORRECTION DÉFINITIVE : Utilisation de la syntaxe env.VARIABLE
                    sh "mvn clean verify sonar:sonar -Dsonar.projectKey=mon-projet -Dsonar.host.url=${env.SONAR_URL} -Dsonar.login=${env.SONAR_AUTH_TOKEN}"
                }
            }
        }

        stage('3. Quality Gate (SonarQube)') {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }

        stage('4. SCA & Build Docker Image (Trivy)') {
            steps {
                script {
                    sh "docker run --rm -v ${env.WORKSPACE}:/path aquasec/trivy:latest fs --exit-code 1 --severity CRITICAL,HIGH /path > trivy-fs-report.txt"
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${env.BUILD_ID} ."
                    sh "docker run --rm aquasec/trivy:latest image --exit-code 1 --severity CRITICAL,HIGH ${DOCKER_IMAGE_NAME}:${env.BUILD_ID} > trivy-image-report.txt"
                }
            }
        }

        stage('5. Deploy to Staging') {
            steps {
                echo "Déploiement de l'image ${DOCKER_IMAGE_NAME}:${env.BUILD_ID} sur staging..."
            }
        }

        stage('6. DAST (OWASP ZAP)') {
            steps {
                sh "docker run --rm -v ${env.WORKSPACE}:/zap/wrk/:rw owasp/zap2docker-stable zap-baseline.py -t ${STAGING_APP_URL} -g gen.conf -r dast-report.html"
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminé. Archivage des rapports de sécurité...'
            archiveArtifacts artifacts: '*.json, *.txt, *.html', allowEmptyArchive: true
        }
        failure {
            echo "Le pipeline a échoué. L'envoi d'email est désactivé."
        }
        success {
            echo 'Pipeline terminé avec succès !'
        }
    }
}
