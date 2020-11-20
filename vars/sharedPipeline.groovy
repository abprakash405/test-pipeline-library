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
			env.ptest = yamlconfig.test[0].testCommand
			env.rtest = yamlconfig.test[1].testCommand
			env.itest = yamlconfig.test[2].testCommand
			env.testFolder = yamlconfig.test[0].testFolder
			
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
		stage ('Test') {
		        parallel 'performance': {
		            bat "echo 'shell scripts to run performance tests...'"
			    bat "${ptest} -f ${testFolder}/pom.xml"
		        },
		        'regression': {
		            bat "echo 'shell scripts to run regression tests...'"
		            bat "${rtest} -f ${testFolder}/pom.xml"
		        },
		        'integration': {
		            bat "echo 'shell scripts to run integration tests...'"
			    bat "${itest} -f ${testFolder}/pom.xml"
		        }
	        }
	    } catch (err) {
	        currentBuild.result = 'FAILED'
	        throw err
	    }
    }
}
