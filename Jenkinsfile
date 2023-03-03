pipeline {

    agent any

    stages {

        stage("Verify tooling") {
            steps {
                sh '''
              cd jenkins
              docker version
              docker info
              docker compose version
              curl --version
              jq --version
              docker compose ps
            '''
            }
        }

        stage('Get code') {
            steps {
               // Get some code from a GitHub repository
               git branch: 'main', url: 'https://github.com/grauds/clematis.weather.api.git'
               sh 'chmod +x gradlew'
            }
        }

        stage('Gradle build') {
            steps {
              sh './gradlew clean build'
            }
        }

        stage('Build docker image') {
            steps {
                sh 'docker build -t clematis.weather.api .'
            }
        }

        stage ('Dependency-Check') {
            steps {
                dependencyCheck additionalArguments: '''
                    -o "./"
                    -s "./"
                    -f "ALL"
                    --prettyPrint''', odcInstallation: 'Dependency Checker'

                dependencyCheckPublisher pattern: 'dependency-check-report.xml'
            }
        }

        stage('Publish tests') {
            steps {
                publishCoverage adapters: [jacocoAdapter('jacoco/jacoco.xml')], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
            }
        }

        stage("Build and start docker compose services") {
          environment {
                SPRING_DATASOURCE_MYSQL_PASSWORD = credentials('SPRING_DATASOURCE_MYSQL_PASSWORD')
          }
          steps {
              sh '''
                 cd jenkins
                 docker compose stop
                 docker stop clematis-weather-api || true && docker rm clematis-weather-api || true
                 docker compose build --build-arg SPRING_DATASOURCE_MYSQL_PASSWORD='$SPRING_DATASOURCE_MYSQL_PASSWORD'
                 docker compose up -d 
              '''
          }
        }
    }

    post {
        always {
            junit '**/build/**/test-results/test/*.xml'
        }
    }
}
