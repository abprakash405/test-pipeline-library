//import groovy.yaml.YamlSlurper
import org.yaml.snakeyaml.Yaml


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
			def yamlconfig = readYaml file: "config.yml"
			env.buildProjectFolder = yamlconfig.build.projectFolder
			env.buildCommand = yamlconfig.build.buildCommand
			env.databaseFolder = yamlconfig.database.databaseFolder
			env.databaseCommand = yamlconfig.database.databaseCommand
			env.deployCommand = yamlconfig.deploy.deployCommand
			
	        }
	        stage ('Build') {
	        	bat "echo 'building ${config.projectName} ...'"
			bat "${buildCommand} -f ${buildProjectFolder}/pom.xml"
			
	        }
	        stage ('Database') {
	        	bat "echo 'building ${config.projectName} ...'"
			bat "${databaseCommand} -f ${databaseFolder}/pom.xml"
			
	        }
	      	stage ('Deploy') {
	            bat "echo 'deploying to server ${config.serverDomain}...'"
		    bat "${deployCommand} -f ${buildProjectFolder}/pom.xml"
	      	}
		stage ('Tests') {
		        parallel 
			'performance': {
		            bat "echo 'shell scripts to run performance tests...'"
		        },
		        'regression': {
		            bat "echo 'shell scripts to run regression tests...'"
		        },
		        'integration': {
		            bat "echo 'shell scripts to run integration tests...'"
		        }
	        }
		stage ('test') {
	            bat "echo 'deploying to server ${config.serverDomain}...'"
		    bat "${deployCommand} -f ${buildProjectFolder}/pom.xml"
	      	}
	    } catch (err) {
	        currentBuild.result = 'FAILED'
	        throw err
	    }
    }
}
