pipeline {
    agent any
    environment {
        SONAR_HOME = tool "Sonar"
    }

    stages {
        stage("Code from github") {
            steps {
                // MODIFICATION APPLIQUÉE ICI
                git url: "https://github.com/moslemhaddadi/DEVOPS.git", branch: "main"
            }
        }

        // --- LES ÉTAPES SUIVANTES SONT INCHANGÉES ---

        stage("SonarQube Quality Analysis" ) {
            steps {
                withSonarQubeEnv("Sonar") {
                    sh "$SONAR_HOME/bin/sonar-scanner -Dsonar.projectName=wanderlust -Dsonar.projectKey=wanderlust "
                }
            }
        }

        stage('Install Node Dependencies') {
            steps {
                sh """
                echo "Installing Node dependencies..."
                cd frontend && npm install || true
                cd ../backend && npm install || true
                """
            }
        }

        stage("OWASP Dependency Check") {
            steps {
                dependencyCheck additionalArguments: '--scan ./', odcInstallation: 'dc'
                dependencyCheckPublisher pattern: ' **/dependency-check-report.xml'
            }
        }

        stage("Sonal Quality Gate Scan") {
            steps {
                timeout(time: 2, unit: "MINUTES") {
                    waitForQualityGate abortPipeline: false
                }
            }
        }

        stage("Trivy File System Scan ") {
            steps {
                sh "trivy fs --format table -o trivy-fs-report.html ."
            }
        }
    }
}
