// Jenkinsfile - Version Corrigée et Complète (avec authentification SonarQube)
pipeline {
    agent any

    environment {
        SONAR_URL = "http://localhost:9000"
        STAGING_APP_URL = "http://staging.mon-app.com"
        DOCKER_IMAGE_NAME = "mon-app"
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
                // 'MySonarQubeServer' doit correspondre au nom configuré dans Manage Jenkins > Configure System.
                withSonarQubeEnv('MySonarQubeServer') {
                    // CORRECTION : Ajout du paramètre -Dsonar.login=${SONAR_AUTH_TOKEN} pour l'authentification.
                    // ${SONAR_AUTH_TOKEN} est une variable spéciale fournie par withSonarQubeEnv.
                    sh "mvn clean verify sonar:sonar -Dsonar.projectKey=mon-projet -Dsonar.host.url=${SONAR_URL} -Dsonar.login=${SONAR_AUTH_TOKEN}"
                }
            }
        }

        stage('3. Quality Gate (SonarQube)') {
            steps {
                // Attend le résultat de l'analyse et bloque le pipeline si la Quality Gate de SonarQube échoue.
                // Cette étape nécessite que l'analyse SAST ait réussi.
                waitForQualityGate abortPipeline: true
            }
        }

        stage('4. SCA & Build Docker Image (Trivy)') {
            steps {
                script {
                    // A. Analyse des dépendances du projet (SCA) avec Trivy.
                    sh "docker run --rm -v ${env.WORKSPACE}:/path aquasec/trivy:latest fs --exit-code 1 --severity CRITICAL,HIGH /path > trivy-fs-report.txt"

                    // B. Construction de l'image Docker.
                    sh "docker build -t ${DOCKER_IMAGE_NAME}:${env.BUILD_ID} ."

                    // C. Scan de l'image Docker fraîchement construite avec Trivy.
                    sh "docker run --rm aquasec/trivy:latest image --exit-code 1 --severity CRITICAL,HIGH ${DOCKER_IMAGE_NAME}:${env.BUILD_ID} > trivy-image-report.txt"
                }
            }
        }

        stage('5. Deploy to Staging') {
            steps {
                echo "Déploiement de l'image ${DOCKER_IMAGE_NAME}:${env.BUILD_ID} sur staging..."
                // sh "./deploy-staging.sh ${DOCKER_IMAGE_NAME}:${env.BUILD_ID}"
            }
        }

        stage('6. DAST (OWASP ZAP)') {
            steps {
                // Lance un scan dynamique sur l'application déployée.
                sh "docker run --rm -v ${env.WORKSPACE}:/zap/wrk/:rw owasp/zap2docker-stable zap-baseline.py -t ${STAGING_APP_URL} -g gen.conf -r dast-report.html"
            }
        }
    } // Fin du bloc 'stages'

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
    } // Fin du bloc 'post'

} // Fin du bloc 'pipeline'
