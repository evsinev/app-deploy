package com.acme.appdeploy.ui.service.appstatus.impl;

import com.acme.appdeploy.dao.config.IConfigAppDao;
import com.acme.appdeploy.dao.config.IConfigAuthDao;
import com.acme.appdeploy.dao.config.model.AppStatusType;
import com.acme.appdeploy.dao.config.model.TAppStatus;
import com.acme.appdeploy.service.appstatus.IAppStatusClient;
import com.acme.appdeploy.service.appstatus.impl.AppStatusClientImpl;
import com.acme.appdeploy.service.appstatus.messages.AppStatusResponse;
import com.acme.appdeploy.service.appstatus.messages.AppStatusResponseType;
import com.acme.appdeploy.ui.service.app.impl.AppFinder;
import com.acme.appdeploy.ui.service.app.impl.AppInstanceFindResult;
import com.acme.appdeploy.ui.service.app.model.ArnViewRequest;
import com.acme.appdeploy.ui.service.appstatus.IUiAppStatusService;
import com.acme.appdeploy.ui.service.appstatus.messages.UiAppStatusResponse;
import com.acme.appdeploy.util.Arns;
import com.acme.appdeploy.util.DateFormats;

public class UiAppStatusServiceImpl implements IUiAppStatusService {

    private final IConfigAppDao  appDao;
    private final IConfigAuthDao authDao;

    private final IAppStatusClient client = new AppStatusClientImpl();

    public UiAppStatusServiceImpl(IConfigAppDao appDao, IConfigAuthDao authDao) {
        this.appDao  = appDao;
        this.authDao = authDao;
    }

    @Override
    public UiAppStatusResponse getInstanceStatus(ArnViewRequest aRequest) {
        AppFinder appFinder = new AppFinder(appDao.listAllApps());

        AppInstanceFindResult appInstance;
        try {
            appInstance = appFinder.findAppInstance(Arns.parseArnInstance(aRequest.getArn()));
        } catch (Exception e) {
            return UiAppStatusResponse.builder()
                    .type(AppStatusResponseType.ERROR)
                    .errorMessage("Instance not found " + aRequest.getArn())
                    .build();
        }

        TAppStatus appStatus = appInstance.getAppInstance().getAppStatus();

        if (appStatus == null) {
            return UiAppStatusResponse.builder()
                    .type(AppStatusResponseType.ERROR)
                    .errorMessage("No appStatus for instance")
                    .build();
        }

        AppStatusResponse appStatusResponse;
        if (appStatus.getType() == AppStatusType.VERSION_TXT) {
            appStatusResponse = client.getVersionTxt(appStatus.getUrl());
        } else {
            String bearerToken = authDao.findAuthById(appStatus.getAuthRef()).getBearerToken();
            appStatusResponse = client.getAppStatus(appStatus.getUrl(), bearerToken);
        }

        return mapToResponse(appStatusResponse, System.currentTimeMillis());
    }

    private UiAppStatusResponse mapToResponse(AppStatusResponse in, long aCheckTime) {

        return UiAppStatusResponse.builder()
                .type                   ( in.getType())
                .errorMessage           ( in.getErrorMessage())
                .appInstanceName        ( in.getAppInstanceName())
                .appVersion             ( in.getAppVersion())
                .hostname               ( in.getHostname())
                .port                   ( in.getPort() > 0 ? in.getPort() : null)
                .responseDateFormatted  ( DateFormats.formatAgo(aCheckTime))
                .uptimeFormatted        ( in.getUptimeMs() <= 0 ? "-" : DateFormats.formatAgo(System.currentTimeMillis() - in.getUptimeMs()))
                .build();
    }
}
