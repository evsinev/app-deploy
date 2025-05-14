package com.acme.appdeploy.dao.deploylog;

import com.acme.appdeploy.dao.deploylog.entity.TDeploy;
import com.acme.appdeploy.dao.deploylog.entity.TDeployLogEvent;
import com.acme.appdeploy.dao.deploylog.model.DeployStatus;

import java.util.List;

public interface IDeployLogDao {

    void createDeploy(TDeploy aDeploy);

    void saveDeploy(TDeploy aDeploy);

    void addDeployLog(TDeployLogEvent aEvent);

    TDeploy getDeployById(String aDeployId);

    List<TDeployLogEvent> getDeployLogEvents(String aDeployId);

    void updateStatus(String aDeployId, DeployStatus aStatus);

    void updateStatusWithMessage(String aDeployId, DeployStatus aStatus, String aMessage);

    List<TDeploy> listRecentDeploys(int aLimit);
}
