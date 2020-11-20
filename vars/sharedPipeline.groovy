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
			println yamlconfig.build
			env.buildCommand = yamlconfig.build.buildCommand
			env.buildProjectFolder = yamlconfig.build.projectFolder
			env.all = yamlconfig
                	
			
	        }
	        stage ('Build') {
	        	bat "echo 'building ${config.projectName} ...'"
			println buildCommand
			bat "${buildCommand} -f ${buildProjectFolder}/pom.xml"
			println all
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
		    println inputconfig.deploy
	      	}
	    } catch (err) {
	        currentBuild.result = 'FAILED'
	        throw err
	    }
    }
}
