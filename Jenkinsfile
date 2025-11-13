// Jenkinsfile - Version Finale et Robuste
pipeline {
    agent any

    environment {
        SONAR_URL = "http://localhost:9000"
        DOCKER_IMAGE_NAME = "mon-app"
    }

    stages {
        // L'étape de checkout est gérée automatiquement par Jenkins
        // lorsque le projet est configuré pour utiliser un SCM.
        // Pas besoin d'une étape 'git' explicite ici.

        stage("1. SAST (SonarQube Analysis )") {
            steps {
                // On combine withSonarQubeEnv (pour la compatibilité avec la Quality Gate)
                // et la commande mvn fiable que nous avons établie.
                withSonarQubeEnv('MySonarQubeServer') {
                    sh "mvn clean verify sonar:sonar -Dsonar.projectKey=mon-projet -Dsonar.host.url=${env.SONAR_URL} -Dsonar.login=${env.SONAR_AUTH_TOKEN}"
                }
            }
        }

        stage("2. Quality Gate (SonarQube)") {
            steps {
                // Attend le verdict de SonarQube avec un timeout de 5 minutes pour éviter un blocage.
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
        stage("3. SCA (Trivy File System Scan)") {
            steps {
                echo "Lancement du scan du système de fichiers avec Trivy..."
                // Scanne le répertoire du projet pour des vulnérabilités et des configurations à risque.
                sh "docker run --rm -v ${env.WORKSPACE}:/path aquasec/trivy:latest fs --format table -o trivy-fs-report.html /path"
            }
        }
        
        // Vous pouvez ajouter ici les étapes de Build Docker, Deploy et DAST quand vous serez prêt.
        
    } // Fin du bloc 'stages'

    post {
        always {
            echo 'Pipeline terminé. Archivage des rapports...'
            // Archive les rapports générés pour consultation.
            archiveArtifacts artifacts: 'trivy-fs-report.html', allowEmptyArchive: true
        }
        failure {
            echo "Le pipeline a échoué." // Pas d'envoi d'email
        }
        success {
            echo 'Pipeline terminé avec succès !'
        }
    } // Fin du bloc 'post'
}
