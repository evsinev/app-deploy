package com.acme.appdeploy.service.deploy;

public interface IDeployService {

    String scheduleDeploy(String aInstanceArn, String aAppVersion);

}
