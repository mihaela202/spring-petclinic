properties([
    parameters([
        string(defaultValue: '', description: 'Please enter VM IP', name: 'nodeIP', trim: true)
        ])
    ])
if (nodeIP.length() > 6) {
    node {
        stage('Pull Repo') {
            git branch: 'master', changelog: false, poll: false, url: 'https://github.com/mihaela202/ansible-petclinic.git'
        }
        withEnv(['ANSIBLE_HOST_KEY_CHECKING=False', 'PETCLINIC_REPO=https://github.com/spring-projects/spring-petclinic.git', 'PETCLINIC_BRANCH=master']) {
            stage("Install Prerequisites"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'prerequisites.yml'
                }
            stage("Pull Repo"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'pull_repo.yml'
                }
            stage("Install Python"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'install-maven.yml'
                }
            stage("Start Flaskex"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'install-java.yml'
                }
            stage("Start Flaskex"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'start-app.yml'
                }
        }  
    }
}
else {
    error 'Please enter valid IP address'
}
