package com.acme.appdeploy.service.deploy.impl;

import com.acme.appdeploy.dao.deploylog.model.DeployStatus;

public interface IDeployContext {

    void updateStatus(DeployStatus aStatus);

    void updateStatusWithMessage(DeployStatus aStatus, String aMessage);

    void debug(String aTemplate, Object ... args);

    void error(String aTemplate, Object ... args);
}
