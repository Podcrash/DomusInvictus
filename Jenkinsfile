pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'chmod +x ./collect.sh'
        sh './collect.sh'
      }
    }

    stage('Archive') {
      steps {
        archiveArtifacts '*.jar'
      }
    }

  }
}