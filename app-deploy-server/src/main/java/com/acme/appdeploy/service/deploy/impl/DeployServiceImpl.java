package com.acme.appdeploy.service.deploy.impl;

import com.acme.appdeploy.dao.config.IConfigAppDao;
import com.acme.appdeploy.dao.deploylog.IDeployLogDao;
import com.acme.appdeploy.dao.deploylog.entity.TDeploy;
import com.acme.appdeploy.dao.deploylog.model.DeployStatus;
import com.acme.appdeploy.service.deploy.IDeployService;
import com.acme.appdeploy.service.http.IHttpService;
import com.acme.appdeploy.ui.service.app.impl.AppFinder;
import com.acme.appdeploy.ui.service.app.impl.AppInstanceFindResult;
import com.acme.appdeploy.ui.service.app.model.ArnViewRequest;
import com.acme.appdeploy.ui.service.appstatus.IUiAppStatusService;
import com.acme.appdeploy.util.Arns;

import java.net.http.HttpClient;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.acme.appdeploy.util.Sequences.nextDeployLogId;

public class DeployServiceImpl implements IDeployService {

    private final IDeployLogDao       deployLogDao;
    private final IConfigAppDao       appDao;
    private final IHttpService        httpService;
    private final IUiAppStatusService appStatusService;


    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();


    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public DeployServiceImpl(IDeployLogDao deployLogDao, IConfigAppDao appDao, IHttpService httpService, IUiAppStatusService appStatusService) {
        this.deployLogDao     = deployLogDao;
        this.appDao           = appDao;
        this.httpService      = httpService;
        this.appStatusService = appStatusService;
    }

    @Override
    public String scheduleDeploy(String aInstanceArn, String aAppVersion) {
        AppFinder             appFinder  = new AppFinder(appDao.listAllApps());
        AppInstanceFindResult findResult = appFinder.findAppInstance(Arns.parseArnInstance(aInstanceArn));

        TDeploy deploy = TDeploy.builder()
                .deployId       ( nextDeployLogId() )
                .appId          ( findResult.getApp().getAppId())
                .envName        ( findResult.getAppEnv().getEnvName())
                .instanceName   ( findResult.getAppInstance().getInstanceName())
                .newVersion     ( aAppVersion )
                .oldVersion     ( fetchOldVersion(aInstanceArn))
                .status         ( DeployStatus.SCHEDULED )
                .build();

        deployLogDao.createDeploy(deploy);

        IDeployContext context = new DeployContextImpl(deploy.getDeployId(), deployLogDao);
        context.debug("Changed status to {}", DeployStatus.SCHEDULED);

        DeployTask task = new DeployTask(
                findResult.getApp()
                , findResult.getAppEnv()
                , findResult.getAppInstance()
                , context
                , httpService
                , httpClient
                , aAppVersion
                , deploy.getDeployId()
        );

        executorService.execute(task);

        return deploy.getDeployId();

    }

    private String fetchOldVersion(String aInstanceArn) {
        return appStatusService.getInstanceStatus(new ArnViewRequest(aInstanceArn))
                .getAppVersion();
    }
}
