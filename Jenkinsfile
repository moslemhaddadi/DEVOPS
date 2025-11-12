// Jenkinsfile - Version finale (SÉCURISÉE - DEVSECOPS)
pipeline {
    agent any // Pour la simplicité, mais un agent Docker spécifique est mieux en production

    environment {
        // URL du serveur SonarQube (à configurer dans Jenkins)
        SONAR_URL = "http://localhost:9000"
        // URL de l'application en staging pour le test DAST
        STAGING_APP_URL = "http://staging.mon-app.com"
    }

    stages {
        // --- ÉTAPE 1 : BUILD ---
        stage('1. Checkout' ) {
            steps {
                git 'https://github.com/moslemhaddadi/DEVOPS.git'
            }
        }

        // --- ÉTAPE 2 : CONTRÔLES DE SÉCURITÉ PRÉ-BUILD ---
        stage('2. Secrets Scan (Gitleaks )') {
            agent { docker { image 'zricethezav/gitleaks:latest' } } // Utilise une image Docker avec Gitleaks
            steps {
                script {
                    try {
                        // Scanne le code pour des secrets. Le pipeline s'arrête si un secret est trouvé.
                        sh 'gitleaks detect --source . --verbose --exit-code 1 --report-path gitleaks-report.json'
                    } catch (Exception e) {
                        // Bloque le build en cas de détection
                        currentBuild.result = 'FAILURE'
                        error("FAIL: Des secrets ont été détectés dans le code ! Rapport disponible.")
                    }
                }
            }
        }

        // --- ÉTAPE 3 : BUILD & ANALYSE STATIQUE ---
        stage('3. SAST (SonarQube)') {
            steps {
                // L'analyse SAST est intégrée à l'étape de build avec Maven
                // Nécessite une configuration de SonarQube dans Jenkins (Manage Jenkins -> Configure System)
                withSonarQubeEnv('MySonarQubeServer') {
                    sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=mon-projet -Dsonar.host.url=${SONAR_URL}'
                }
            }
        }

        stage('4. Quality Gate (SonarQube)') {
            steps {
                // Attend le résultat de l'analyse SonarQube et bloque le pipeline si la "Quality Gate" échoue
                // (ex: trop de bugs, failles critiques, etc.)
                waitForQualityGate abortPipeline: true
            }
        }

        // --- ÉTAPE 4 : ANALYSE DES DÉPENDANCES ET DE L'IMAGE ---
        stage('5. SCA & Build Docker Image (Trivy)') {
            steps {
                script {
                    // A. Analyse des dépendances du projet (ex: pom.xml)
                    // Utilise une image Docker avec Trivy
                    docker.image('aquasec/trivy:latest').inside {
                        // Bloque si des vulnérabilités critiques sont trouvées dans les dépendances
                        sh 'trivy fs --exit-code 1 --severity CRITICAL,HIGH . > trivy-fs-report.txt'
                    }

                    // B. Construction de l'image Docker
                    def dockerImage = docker.build("mon-app:${env.BUILD_ID}")

                    // C. Scan de l'image Docker fraîchement construite
                    // Bloque si des vulnérabilités critiques sont trouvées dans l'OS ou les librairies de l'image
                    sh "trivy image --exit-code 1 --severity CRITICAL,HIGH mon-app:${env.BUILD_ID} > trivy-image-report.txt"
                }
            }
        }

        // --- ÉTAPE 5 : DÉPLOIEMENT ET ANALYSE DYNAMIQUE ---
        stage('6. Deploy to Staging') {
            steps {
                echo "Déploiement de l'image mon-app:${env.BUILD_ID} sur staging..."
                // sh "./deploy-staging.sh mon-app:${env.BUILD_ID}"
            }
        }

        stage('7. DAST (OWASP ZAP)') {
            agent { docker { image 'owasp/zap2docker-stable' } } // Utilise l'image Docker d'OWASP ZAP
            steps {
                // Lance un scan DAST de base sur l'application déployée en staging
                sh 'zap-baseline.py -t ${STAGING_APP_URL} -g gen.conf -r dast-report.html'
            }
        }
    }

    // --- ÉTAPE 6 : REPORTING & ALERTING ---
    post {
        always {
            echo 'Pipeline terminé. Archivage des rapports...'
            // Archive tous les rapports générés pour consultation ultérieure
            archiveArtifacts artifacts: '*.json, *.txt, *.html', allowEmptyArchive: true
        }
        failure {
            // Envoie une notification en cas d'échec du pipeline
            mail to: 'equipe-dev@example.com',
                 subject: "ÉCHEC Pipeline: ${currentBuild.fullDisplayName}",
                 body: "Le pipeline a échoué à l'étape : ${currentBuild.currentResult}. Consultez les logs : ${env.BUILD_URL}"
        }
        success {
            echo 'Pipeline réussi !'
        }
    }
}
