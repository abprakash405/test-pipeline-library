def call(body) {

  pipeline {
      agent any

    triggers {
      githubPush()
    }


    stages {
      stage("Pipeline") {
          stage("build") {
            stages {
              stage('build') {
                steps {
                    sh "echo hello world, Lint image"
                }
              }
            }
          }
        }
    }
  }
}
