#!/usr/bin/env groovy

node('iasset-jenkins-slave') {

    // -----------------------------------------------
    // --------------- Staging Branch ----------------
    // -----------------------------------------------
    if (env.BRANCH_NAME == 'staging') {

        stage('SCM Checkout') {
            checkout([$class: 'GitSCM',
                branches: [[name: '${BRANCH_NAME}']],
                extensions: [[$class: 'SubmoduleOption', recursiveSubmodules: true ]],
                userRemoteConfigs: [[url: 'https://github.com/i-Asset/identity-service.git']]
            ])
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

        stage('SCM Checkout') {
            checkout([$class: 'GitSCM',
                branches: [[name: '${BRANCH_NAME}']],
                extensions: [[$class: 'SubmoduleOption', recursiveSubmodules: true ]],
                userRemoteConfigs: [[url: 'https://github.com/i-Asset/identity-service.git']]
           ])
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
            checkout([$class: 'GitSCM',
                branches: [[name: '${BRANCH_NAME}']],
                extensions: [[$class: 'SubmoduleOption', recursiveSubmodules: true ]],
                userRemoteConfigs: [[url: 'https://github.com/i-Asset/identity-service.git']]
            ])
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
