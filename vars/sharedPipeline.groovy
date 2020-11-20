def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
	    // Clean workspace before doing anything
	    deleteDir()

	    try {
	        stage ('Clone') {
	        	checkout scm
	        }
	        stage ('Build') {
	        	bat "echo 'building ${config.projectName} ...'"
	        }
	        stage ('Tests') {
		        parallel 'static': {
		            bat "echo 'shell scripts to run static tests...'"
		        },
		        'unit': {
		            bat "echo 'shell scripts to run unit tests...'"
		        },
		        'integration': {
		            bat "echo 'shell scripts to run integration tests...'"
		        }
	        }
	      	stage ('Deploy') {
	            bat "echo 'deploying to server ${config.serverDomain}...'"
	      	}
	    } catch (err) {
	        currentBuild.result = 'FAILED'
	        throw err
	    }
    }
}
