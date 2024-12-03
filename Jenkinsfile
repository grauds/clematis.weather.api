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
        git branch: 'main', url: 'https://github.com/grauds/clematis.weather.api.git'
         sh 'chmod +x gradlew'
      }
    }

        stage('Gradle build') {
            steps {
              sh './gradlew clean build'
            }

        }

        stage ('Dependency-Check') {
            steps {
                dependencyCheck additionalArguments: '''
                    -o "./"
                    -s "./"
                    -f "ALL"
                    --prettyPrint''', nvdCredentialsId: 'NVD_API_Key', odcInstallation: 'Dependency Checker'

                dependencyCheckPublisher pattern: 'dependency-check-report.xml'
            }
        }

        stage('Publish tests') {
            steps {
                recordCoverage(tools: [[parser: 'JACOCO']],
                        id: 'jacoco', name: 'JaCoCo Coverage',
                        sourceCodeRetention: 'EVERY_BUILD',
                        qualityGates: [
                                [threshold: 60.0, metric: 'LINE', baseline: 'PROJECT', unstable: true],
                                [threshold: 60.0, metric: 'BRANCH', baseline: 'PROJECT', unstable: true]])
            }
        }

        stage('Build docker image') {
            steps {
                sh 'docker build -t clematis.weather.api .'
            }
        }

        stage("Build and start docker compose services") {
          environment {
              SPRING_DATASOURCE_PASSWORD = credentials('SPRING_DATASOURCE_PASSWORD')
              WEATHER_IMAGES_PATH = credentials('WEATHER_IMAGES_PATH')
          }
          steps {
              sh '''
                 cd jenkins
                 docker compose stop
                 docker stop clematis-weather-api || true && docker rm clematis-weather-api || true
                 docker compose build --build-arg SPRING_DATASOURCE_PASSWORD='SPRING_DATASOURCE_PASSWORD' --build-arg WEATHER_IMAGES_PATH='$WEATHER_IMAGES_PATH'
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
