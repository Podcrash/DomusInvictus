pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh '''chmod +x ./collect.sh
chmod +x ./gradlew'''
        sh 'bash collect.sh spigot'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts '*.jar'
      }
    }

  }
}