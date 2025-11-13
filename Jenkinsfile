// Jenkinsfile - Version Corrigée (sans le bloc 'tools')
pipeline {
    agent any

    environment {
        SONAR_URL = "http://localhost:9000"
    }

    // Le bloc 'tools' a été supprimé car 'dependencyCheck' n'est pas un type d'outil valide ici.

    stages {
        stage("1. Checkout Code from GitHub" ) {
            steps {
                git url: "https://github.com/moslemhaddadi/DEVOPS.git", branch: "main"
            }
        }

        stage("2. SAST (SonarQube Analysis )") {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    sh "mvn clean verify sonar:sonar -Dsonar.projectKey=mon-projet -Dsonar.host.url=${env.SONAR_URL} -Dsonar.login=${env.SONAR_AUTH_TOKEN}"
                }
            }
        }

        stage("3. Quality Gate (SonarQube)") {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage("4. SCA (OWASP Dependency Check)") {
            steps {
                // 'dc' doit correspondre au nom de l'installation configurée dans Manage Jenkins > Global Tool Configuration.
                dependencyCheck additionalArguments: '--scan . --format HTML --format XML', odcInstallation: 'dc'
            }
        }
        
        stage("5. SCA (Trivy File System Scan)") {
            steps {
                sh "docker run --rm -v ${env.WORKSPACE}:/path aquasec/trivy:latest fs --format table -o trivy-fs-report.html /path"
            }
        }
    }

    post {
        always {
            echo 'Pipeline terminé. Archivage des rapports...'
            // Publie le rapport XML pour que Jenkins puisse afficher les graphiques de tendances.
            dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
            // Archive les rapports HTML pour consultation manuelle.
            archiveArtifacts artifacts: '**/dependency-check-report.html, trivy-fs-report.html', allowEmptyArchive: true
        }
        failure {
            echo "Le pipeline a échoué."
        }
        success {
            echo 'Pipeline terminé avec succès !'
        }
    }
}
