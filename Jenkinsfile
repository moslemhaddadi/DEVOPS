// Jenkinsfile - Version Corrigée et Complète (2025-11-12)
pipeline {
    // Utilise n'importe quel agent disponible.
    // Assurez-vous que cet agent a Docker installé et que l'utilisateur 'jenkins' a le droit de l'utiliser.
    agent any

    environment {
        // URL de votre serveur SonarQube. Assurez-vous qu'il est accessible depuis Jenkins.
        SONAR_URL = "http://localhost:9000"
        // URL de l'application une fois déployée en staging (pour le test DAST )
        STAGING_APP_URL = "http://staging.mon-app.com"
        // Nom de l'image Docker qui sera construite
        DOCKER_IMAGE_NAME = "mon-app"
    }

    stages {
        // L'étape de checkout initiale est supprimée car Jenkins le fait déjà implicitement.
        // Le pipeline commence directement par les étapes de sécurité.

        stage('1. Secrets Scan (Gitleaks )') {
            steps {
                script {
                    try {
                        // Exécute Gitleaks dans un conteneur Docker pour scanner le code source.
                        // Le volume -v monte le répertoire du projet dans le conteneur pour l'analyse.
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
                // Analyse le code avec SonarQube via Maven.
                // 'MySonarQubeServer' doit être configuré dans Manage Jenkins > Configure System > SonarQube servers.
                withSonarQubeEnv('MySonarQubeServer') {
                    sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=mon-projet -Dsonar.host.url=${SONAR_URL}'
                }
            }
        }

        stage('3. Quality Gate (SonarQube)') {
            steps {
                // Attend le résultat de l'analyse et bloque le pipeline si la Quality Gate de SonarQube échoue.
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
                // Le conteneur ZAP doit pouvoir accéder à l'URL de staging.
                sh "docker run --rm -v ${env.WORKSPACE}:/zap/wrk/:rw owasp/zap2docker-stable zap-baseline.py -t ${STAGING_APP_URL} -g gen.conf -r dast-report.html"
            }
        }
    } // Fin du bloc 'stages'

    post {
        always {
            echo 'Pipeline terminé. Archivage des rapports de sécurité...'
            // Archive tous les rapports générés pour les consulter dans l'interface Jenkins.
            archiveArtifacts artifacts: '*.json, *.txt, *.html', allowEmptyArchive: true
        }
        failure {
            // La notification par email est désactivée pour éviter une erreur si le serveur SMTP n'est pas configuré.
            echo "Le pipeline a échoué. L'envoi d'email est désactivé."
        }
        success {
            echo 'Pipeline terminé avec succès !'
        }
    } // Fin du bloc 'post'

} // Fin du bloc 'pipeline'
