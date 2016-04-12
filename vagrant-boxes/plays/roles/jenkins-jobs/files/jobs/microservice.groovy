class ProjectDef {
	String name
	String repo
	String defaultGitBranch = 'master'
	boolean isMicroservice = true
	String mavenSubmodule = null
}

def projects = [
		new ProjectDef(name: 'sensor-config', repo: 'https://github.com/djalexd/microservices-masterpom.git', mavenSubmodule: 'sensor-config'),
		new ProjectDef(name: 'sensor-alerts', repo: 'https://github.com/djalexd/microservices-masterpom.git', mavenSubmodule: 'sensor-alergs')
]

def separateJobsByFunctionality = true
def buildFolder = 'build-stage'
def deployFolder = 'deploy-stage'

projects.each {

	folder("${it.name}") {
		displayName("Microservice ${it.name}")
		description("Folder for microservice ${it.name}")
	}

	folder("${it.name}/${buildFolder}") {
		displayName('Build stage')
	}

	folder("${it.name}/${deployFolder}") {
		displayName('Deploy stage')
	}

	def buildJobs = createSpringBootMicroServiceBuildPipeline(it, buildFolder)

	def devDeployJobs = createSpringBootMicroServiceEnvDeployPipeline(it, deployFolder, 'dev')

	// Build view.
	deliveryPipelineView("${it.name}/pipelines") {
		pipelineInstances(1)
		showAggregatedPipeline()
		columns(2)
		sorting(Sorting.TITLE)
		updateInterval(1)
		enableManualTriggers()
		showAvatars()
		showChangeLog()
		pipelines {
			component("${buildFolder}", buildJobs[0].name)
			component("${deployFolder}", devDeployJobs[0].name)
		}
	}

}


/**
 * Configures build pipeline for a single microservice. In effect, this method will
 * create jobs, views and triggers that will SCM poll on target repo, and finish by
 * promoting generated artifacts (e.g. our particular design will generate a JAR,
 * a JAR source and a Docker image). Some more complex
 *
 * @param projectName Name of this project, cannot contain characters that
 * are considered unsafe by Jenkins (e.g. / )
 * @param gitRepo Git repository
 * @param branch Branch used to checkout code. If not provided, this will default to 'master'
 * @param gitCredentials Optionally, git credentials (need to be configured in Jenkins credentials,
 * the parameter passed here is actually ID of those credentials).
 * @return
 *  List of generated jobs
 */
def createSpringBootMicroServiceBuildPipeline(def project, def folder, def gitCredentials = null) {

	if (!project) {
		throw new IllegalArgumentException("Project must be specified")
	}

	// Create jobs.
	def job1 = createStartJob(project, folder, gitCredentials)
	def job2 = createTestJob(project, folder, gitCredentials)
	def job3 = createIntegrationTestJob(project, folder, gitCredentials)
	def job4 = createPackageJob(project, folder, gitCredentials)
	def job5 = createDockerPackageJob(project, folder, gitCredentials)
	def job6 = createUploadJob(project, folder, gitCredentials)
	def job7 = createBuildPipelinePromotionJob(project, folder, gitCredentials)

	// Chain the jobs created above.
	def jobs = Arrays.asList(job1, job2, job3, job4, job5, job6, job7)
	configureScm(jobs, project.repo, project.defaultGitBranch)
	configureLogRotate(jobs, -1, 20)
	chainJobs(jobs)

	// Returns jobs.
	jobs
}


/**
 * Deploys artifacts for a particular microservice, identified by projectName. The deployment itself
 * is described with details <a href=....>here</a>.
 * @param projectName
 * @param environment
 */
def createSpringBootMicroServiceEnvDeployPipeline(def project, def folder, def environment) {

	if (!environment) {
		throw new IllegalArgumentException("Environment must be specified")
	}

	// Create jobs.
	def job1 = createDeployStartJob(project, folder, environment)
	def job2 = createDbMigrationJob(project, folder, environment)
	// todo how to retrieve what docker containers to run?
	// todo how to retrieve runtime parameters?
	def job3 = createDeploymentJob(project, folder, environment)
	def job4 = createPostDeploymentSmokeTestJob(project, folder, environment)
	def job5 = createPostDeploymentAcceptanceTestJob(project, folder, environment)
	def job6 = createDeployPipelinePromotionJob(project, folder, environment)

	// Chain the jobs created above.
	def jobs = Arrays.asList(job1, job2, job3, job4, job5, job6)
	configureLogRotate(jobs, -1, 20)
	chainJobs(jobs)

	// Finish.
	jobs
}


def createStartJob(def project, def folder, def gitCredentials = null) {

	return job("${project.name}/${folder}/Start") {
		displayName("Start job for $project.name")
		logRotator {
			daysToKeep(-1)
			numToKeep(10)
		}

		triggers {
			cron("H/15 * * * *")
		}

		wrappers {
			preBuildCleanup()
		}

		// the swarm label is a future extension
		/*label("swarm")*/

		deliveryPipelineConfiguration('start')

		publishers {

		}
	}
}


def createTestJob(def project, def folder, def gitCredentials = null) {
	return job("${project.name}/${folder}/Unit tests") {
		deliveryPipelineConfiguration('tests')

		steps {
			if (project.mavenSubmodule) {
				maven("-B -pl ${project.mavenSubmodule} clean test")
			} else {
				maven("-B clean test")
			}
		}

		publishers {
		}
	}
}


def createIntegrationTestJob(def project, def folder, def gitCredentials = null) {
	return job("${project.name}/build-stage/Component tests") {
		deliveryPipelineConfiguration('tests')

		publishers {

		}
	}
}


def createPackageJob(def project, def folder, def gitCredentials = null) {
	return job("${project.name}/${folder}/Package") {
		deliveryPipelineConfiguration('package')

		publishers {

		}
	}

}

def createDockerPackageJob(def project, def folder, def gitCredentials = null) {
	return job("${project.name}/${folder}/Create docker image") {
		deliveryPipelineConfiguration('package')

		publishers {

		}
	}

}

def createUploadJob(def project, def folder, def gitCredentials = null) {

	return job("${project.name}/${folder}/Upload artifacts") {
		deliveryPipelineConfiguration('package')
		publishers {

		}
	}
}


def createBuildPipelinePromotionJob(def project, def folder, def gitCredentials = null) {

	return job("${project.name}/${folder}/Promotion") {
		deliveryPipelineConfiguration('promote')
		publishers {

		}

	}
}


def createDeployStartJob(project, def folder, environment) {
	return job("${project.name}/${folder}/${environment} - Start") {

	}
}

def createDbMigrationJob(project, def folder, environment) {
	return job("${project.name}/${folder}/${environment} - DB Migration") {

	}
}

def createDeploymentJob(project, def folder, environment) {
	return job("${project.name}/${folder}/${environment} - Deploy") {

	}
}

def createPostDeploymentSmokeTestJob(project, def folder, environment) {
	return job("${project.name}/${folder}/${environment} - Post deploy smoke tests") {

	}
}

def createPostDeploymentAcceptanceTestJob(project, def folder, environment) {
	return job("${project.name}/${folder}/${environment} - Acceptance tests") {

	}
}

def createDeployPipelinePromotionJob(project, def folder, environment) {
	return job("${project.name}/${folder}/${environment} - Artifact promotion") {

	}
}

/* Utility methods */


/**
 * chains a couple of jobs by attaching 'downstreamParameterized' to each job
 * besides the last one.
 * @param jobs A list of jobs
 */
def chainJobs(def jobs) {

	for (i = 0; i < jobs.size() - 1; i++) {
		def currentJob = jobs.get(i), nextJob = jobs.get(i + 1)

		println 'Linking ${nextJob.name} as downstream project of ${currentJob.name}'

		currentJob.configure({
			it / 'publishers' / 'hudson.plugins.parameterizedtrigger.BuildTrigger'(plugin: 'parameterized-trigger@2.30') / 'configs' / 'hudson.plugins.parameterizedtrigger.BuildTriggerConfig' {
				configs {
					'hudson.plugins.parameterizedtrigger.CurrentBuildParameters'
				}
				projects nextJob.name
				condition 'UNSTABLE_OR_BETTER'
				triggerWithNoParameters 'false'
			}

		})

	}
}


def configureLogRotate(def jobs, def days, def num) {

	jobs.each { j ->

		j.configure({
			it / 'logRotator' {
				daysToKeep days
				numToKeep num
				artifactDaysToKeep -1
				artifactNumToKeep -1
			}
		})

	}
}


def configureScm(def jobs, def scmUrl, def branch) {
	jobs.each { j ->
		j.configure({
			it / 'scm'(class:'hudson.plugins.git.GitSCM') {
				userRemoteConfigs {
					'hudson.plugins.git.UserRemoteConfig' {
						url scmUrl
					}
				}
				branches {
					'hudson.plugins.git.BranchSpec' {
						name branch
					}
				}
				configVersion 2
				disableSubmodules false
				recursiveSubmodules false
				doGenerateSubmoduleConfigurations false
				authorOrCommitter false
				clean false
				wipeOutWorkspace false
				pruneBranches true
				remotePoll false
				ignoreNotifyCommit false
				gitTool 'Default'
				skipTag true
			}
		})
	}
}