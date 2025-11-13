// Jenkinsfile - Version Corrigée (avec la syntaxe du timeout corrigée)
pipeline {
    agent any

    environment {
        SONAR_URL = "http://localhost:9000"
    }

    // Outils à configurer dans Jenkins > Global Tool Configuration
    // 'dc' doit être le nom de votre installation Dependency-Check
    tools {
        dependencyCheck 'dc'
    }

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
                // CORRECTION : Ajout de la virgule entre les paramètres time et unit.
                timeout(time: 2, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage("4. SCA (OWASP Dependency Check)") {
            steps {
                dependencyCheck additionalArguments: '--scan . --format HTML --format XML', odcInstallation: 'dc'
                // L'étape de publication des résultats est généralement faite dans la section 'post'
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
            // On publie le rapport Dependency-Check ici.
            dependencyCheckPublisher pattern: '**/dependency-check-report.xml'
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
