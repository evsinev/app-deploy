package com.acme.appdeploy.ui.service.deploy.impl;

import com.acme.appdeploy.dao.config.IConfigAppDao;
import com.acme.appdeploy.dao.deploylog.IDeployLogDao;
import com.acme.appdeploy.dao.deploylog.entity.TDeploy;
import com.acme.appdeploy.dao.deploylog.entity.TDeployLogEvent;
import com.acme.appdeploy.service.deploy.IDeployService;
import com.acme.appdeploy.ui.service.appstatus.IUiAppStatusService;
import com.acme.appdeploy.ui.service.deploy.IUiDeployService;
import com.acme.appdeploy.ui.service.deploy.messages.*;
import com.acme.appdeploy.ui.service.deploy.model.DeployListItem;
import com.acme.appdeploy.ui.service.deploy.model.DeployLogItem;
import com.acme.appdeploy.util.Arns;
import com.acme.appdeploy.util.StatusIndicatorType;

import java.util.List;

import static com.acme.appdeploy.util.DateFormats.formatEpoch;
import static com.acme.appdeploy.util.InstancePaths.instancePath;

public class UiDeployServiceImpl implements IUiDeployService {

    private final IDeployService      deployService;
    private final IDeployLogDao       deployLogDao;

    public UiDeployServiceImpl(IDeployService deployService, IDeployLogDao deployLogDao) {
        this.deployService = deployService;
        this.deployLogDao  = deployLogDao;
    }

    @Override
    public RunDeployResponse runDeploy(RunDeployRequest aRequest) {
        String deployId = deployService.scheduleDeploy(aRequest.getInstanceArn(), aRequest.getAppVersion());

        return RunDeployResponse.builder()
                .deployId(deployId)
                .build();
    }

    @Override
    public DeployViewResponse viewDeploy(DeployViewRequest aRequest) {
        TDeploy               deploy = deployLogDao.getDeployById(aRequest.getDeployId());
        List<TDeployLogEvent> events = deployLogDao.getDeployLogEvents(aRequest.getDeployId());

        return DeployViewResponse.builder()
                .deployId        ( aRequest.getDeployId())
                .appId           ( deploy.getAppId())
                .envName         ( deploy.getEnvName())
                .instanceName    ( deploy.getInstanceName())
                .instanceArn     ( Arns.instanceArn(deploy.getAppId(), deploy.getEnvName(), deploy.getInstanceName()))
                .instancePath    ( instancePath(deploy.getAppId(), deploy.getEnvName(), deploy.getInstanceName()))
                .newVersion      ( deploy.getNewVersion())
                .oldVersion      ( deploy.getOldVersion())
                .logs            ( toLogItems(events))
                .status          ( deploy.getStatus())
                .statusIndicator ( deploy.getStatus() != null ? deploy.getStatus().getIndicator() : StatusIndicatorType.INDICATOR_WARNING)
                .build();
    }

    @Override
    public DeployListResponse listRecentDeploys(DeployListRequest aRequest) {
        List<DeployListItem> deploys = deployLogDao.listRecentDeploys(aRequest.getCount())
                .stream()
                .map(it -> DeployListItem.builder()
                        .deployId            ( it.getDeployId())
                        .appId               ( it.getAppId())
                        .envName             ( it.getEnvName())
                        .instanceName        ( it.getInstanceName())
                        .instanceArn         ( Arns.instanceArn(it.getAppId(), it.getEnvName(), it.getInstanceName()))
                        .newVersion          ( it.getNewVersion())
                        .oldVersion          ( it.getOldVersion())
                        .status              ( it.getStatus())
                        .statusIndicator     ( it.getStatus() != null ? it.getStatus().getIndicator() : StatusIndicatorType.INDICATOR_WARNING)
                        .statusErrorMessage  ( it.getStatusErrorMessage())
                        .build()
                )
                .toList();

        return DeployListResponse.builder()
                .deploys(deploys)
                .build();
    }

    private List<DeployLogItem> toLogItems(List<TDeployLogEvent> events) {
        return events.stream()
                .map(it -> DeployLogItem.builder()
                        .level(it.getLevel())
                        .message(it.getMessage())
                        .dateFormatted(formatEpoch(it.getEpoch()))
                        .build()
                )
                .toList();
    }
}
