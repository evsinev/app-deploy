package com.acme.appdeploy.ui.service.deploy;

import com.acme.appdeploy.ui.service.deploy.messages.*;

public interface IUiDeployService {

    RunDeployResponse runDeploy(RunDeployRequest aRequest);

    DeployViewResponse viewDeploy(DeployViewRequest aRequest);

    DeployListResponse listRecentDeploys(DeployListRequest aRequest);
}
