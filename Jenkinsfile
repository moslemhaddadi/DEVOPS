// Jenkinsfile - Version Corrigée (sans plugin Docker Pipeline)
pipeline {
    // On définit un agent global. Toutes les étapes s'exécuteront sur cet agent.
    agent any

    environment {
           SONARQUBE_URL = 'http://localhost:9000'
        PATH = "$PATH:/var/lib/jenkins/.local/bin"
        BUILD_TIMESTAMP = new Date().format("yyyy-MM-dd'T'HH:mm:ssXXX")
    }
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
            steps {
                script {
                    try {
                        // CORRECTION : On exécute Gitleaks via une commande 'docker run' manuelle.
                        // --rm : supprime le conteneur après exécution.
                        // -v ${env.WORKSPACE}:/path : monte le répertoire du projet Jenkins dans le conteneur.
                        sh "docker run --rm -v ${env.WORKSPACE}:/path zricethezav/gitleaks:latest detect --source=/path --verbose --exit-code 1 --report-path /path/gitleaks-report.json"
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("FAIL: Des secrets ont été détectés dans le code ! Rapport disponible.")
                    }
                }
            }
        }

        // --- ÉTAPE 3 : BUILD & ANALYSE STATIQUE ---
        stage('3. SAST (SonarQube)') {
            // Pas de changement ici, car cette étape n'utilisait pas d'agent Docker.
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    sh 'mvn clean verify sonar:sonar -Dsonar.projectKey=mon-projet -Dsonar.host.url=${SONAR_URL}'
                }
            }
        }

        stage('4. Quality Gate (SonarQube)') {
            // Pas de changement ici.
            steps {
                waitForQualityGate abortPipeline: true
            }
        }

        // --- ÉTAPE 4 : ANALYSE DES DÉPENDANCES ET DE L'IMAGE ---
        stage('5. SCA & Build Docker Image (Trivy)') {
            steps {
                script {
                    // A. Analyse des dépendances du projet (SCA)
                    // CORRECTION : On exécute Trivy via une commande 'docker run' manuelle.
                    sh "docker run --rm -v ${env.WORKSPACE}:/path aquasec/trivy:latest fs --exit-code 1 --severity CRITICAL,HIGH /path > trivy-fs-report.txt"

                    // B. Construction de l'image Docker
                    // Pas de changement ici, la syntaxe docker.build() est fournie par le plugin Docker Pipeline,
                    // mais elle est souvent disponible même si 'agent { docker }' ne l'est pas.
                    // Si cette ligne échoue aussi, il faudra la remplacer par 'sh "docker build -t mon-app:${env.BUILD_ID}" .'
                    def dockerImage = docker.build("mon-app:${env.BUILD_ID}")

                    // C. Scan de l'image Docker fraîchement construite
                    // CORRECTION : On utilise une commande 'sh' standard.
                    sh "docker run --rm aquasec/trivy:latest image --exit-code 1 --severity CRITICAL,HIGH mon-app:${env.BUILD_ID} > trivy-image-report.txt"
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
            steps {
                // CORRECTION : On exécute ZAP via une commande 'docker run' manuelle.
                // On doit s'assurer que le conteneur ZAP peut atteindre l'URL de l'application.
                // L'option --network="host" peut être nécessaire si l'app tourne sur localhost.
                sh "docker run --rm -v ${env.WORKSPACE}:/zap/wrk/:rw owasp/zap2docker-stable zap-baseline.py -t ${STAGING_APP_URL} -g gen.conf -r dast-report.html"
            }
        }
    }

    // --- ÉTAPE 6 : REPORTING & ALERTING ---
    post {
        // Pas de changement dans cette section.
        always {
            echo 'Pipeline terminé. Archivage des rapports...'
            archiveArtifacts artifacts: '*.json, *.txt, *.html', allowEmptyArchive: true
        }
        failure {
            mail to: 'equipe-dev@example.com',
                 subject: "ÉCHEC Pipeline: ${currentBuild.fullDisplayName}",
                 body: "Le pipeline a échoué à l'étape : ${currentBuild.currentResult}. Consultez les logs : ${env.BUILD_URL}"
        }
        success {
            echo 'Pipeline réussi !'
        }
    }
}
