#!/usr/bin/env groovy

node('iasset-jenkins-slave') {

    // -----------------------------------------------
    // --------------- Staging Branch ----------------
    // -----------------------------------------------
    if (env.BRANCH_NAME == 'staging') {

        stage('Clone and Update') {
            git(url: 'https://github.com/i-Asset/identity-service.git', branch: env.BRANCH_NAME)
            def exists = fileExists 'solr-model'
            if (!exists) {
                sh 'git clone https://github.com/i-Asset/solr-model.git'
                sh 'cd solr-model && git checkout staging && mvn clean install'
            }
            sh 'cd ..'
            sh 'git submodule init'
            sh 'git submodule update'
        }

        stage('Build Dependencies') {
            sh 'rm -rf common'
            sh 'git clone https://github.com/i-Asset/common.git'
            dir('common') {
                sh 'git checkout ' + env.BRANCH_NAME
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Build Java') {
            sh 'mvn clean install -DskipTests'
        }

        stage('Build Docker') {
            sh 'mvn -f identity-service/pom.xml docker:build -DdockerImageTag=staging'
        }

        stage('Push Docker') {
            sh 'docker push iassetplatform/identity-service:staging'
        }

        stage('Deploy') {
            sh 'ssh staging "cd /srv/docker-setup/staging/ && ./run-staging.sh restart-single identity-service"'
        }
    }

    // -----------------------------------------------
    // ---------------- Master Branch ----------------
    // -----------------------------------------------
    if (env.BRANCH_NAME == 'master') {

        stage('Clone and Update') {
            git(url: 'https://github.com/i-Asset/identity-service.git', branch: env.BRANCH_NAME)
            sh 'git submodule init'
            sh 'git submodule update'
        }

        stage('Build Dependencies') {
            sh 'rm -rf common'
            sh 'git clone https://github.com/i-Asset/common.git'
            dir('common') {
                sh 'git checkout ' + env.BRANCH_NAME
                sh 'mvn clean install -DskipTests'
            }
        }


        stage('Build Java') {
            sh 'mvn clean install -DskipTests'
        }
    }

    // -----------------------------------------------
    // ---------------- Release Tags -----------------
    // -----------------------------------------------
    if( env.TAG_NAME ==~ /^\d+.\d+.\d+$/) {

        stage('Clone and Update') {
            git(url: 'https://github.com/i-Asset/identity-service.git', branch: 'master')
            sh 'git submodule init'
            sh 'git submodule update'
        }
        stage('Set version') {
            sh 'mvn org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=' + env.TAG_NAME
            sh 'mvn -f identity-service/pom.xml org.codehaus.mojo:versions-maven-plugin:2.1:set -DnewVersion=' + env.TAG_NAME
        }

        //stage('Run Tests') {
        //    sh 'mvn clean test'
        //}

        stage('Build Java') {
            sh 'mvn clean install -DskipTests'
        }

        stage('Build Docker') {
            sh 'mvn -f identity-service/pom.xml docker:build'
        }

        stage('Push Docker') {
            sh 'docker push iassetplatform/identity-service:' + env.TAG_NAME
            sh 'docker push iassetplatform/identity-service:latest'
        }

        stage('Deploy MVP') {
            sh 'ssh nimble "cd /data/deployment_setup/prod/ && sudo ./run-prod.sh restart-single identity-service"'
        }
    }
}
