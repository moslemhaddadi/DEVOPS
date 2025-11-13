// Jenkinsfile - Version Robuste et Adaptée à votre projet
pipeline {
    agent any

    environment {
        SONAR_URL = "http://localhost:9000"
    }

    stages {
        stage("1. SAST (SonarQube Analysis )") {
            steps {
                // On combine withSonarQubeEnv (pour la Quality Gate)
                // et la commande mvn fiable que nous avons établie.
                withSonarQubeEnv('MySonarQubeServer') {
                    sh "mvn clean verify sonar:sonar -Dsonar.projectKey=mon-projet -Dsonar.host.url=${env.SONAR_URL} -Dsonar.login=${env.SONAR_AUTH_TOKEN}"
                }
            }
        }

        stage("2. Quality Gate (SonarQube)") {
            steps {
                // Attend le verdict de SonarQube avec un timeout pour éviter un blocage.
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // Ajoutez ici les autres étapes que vous voulez conserver, comme Trivy.
        stage("3. SCA (Trivy File System Scan)") {
            steps {
                sh "docker run --rm -v ${env.WORKSPACE}:/path aquasec/trivy:latest fs --format table -o trivy-fs-report.html /path"
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminé. Archivage des rapports...'
            archiveArtifacts artifacts: 'trivy-fs-report.html', allowEmptyArchive: true
        }
        failure {
            echo "Le pipeline a échoué."
        }
        success {
            echo 'Pipeline terminé avec succès !'
        }
    }
}
