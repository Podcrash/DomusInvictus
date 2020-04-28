pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh '''chmod +x ./collect.sh
chmod +x ./gradlew'''
        sh './collect.sh spigot'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts '*.jar'
      }
    }

  }
}