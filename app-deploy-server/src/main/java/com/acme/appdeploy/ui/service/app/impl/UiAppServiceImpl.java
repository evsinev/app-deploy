package com.acme.appdeploy.ui.service.app.impl;

import com.acme.appdeploy.dao.config.IConfigAppDao;
import com.acme.appdeploy.ui.service.app.IUiAppService;
import com.acme.appdeploy.ui.service.app.messages.UiAppListResponse;
import com.acme.appdeploy.ui.service.app.messages.UiAppViewResponse;
import com.acme.appdeploy.ui.service.app.model.ArnViewRequest;
import com.acme.appdeploy.util.ArnAppInstance;
import com.payneteasy.apiservlet.VoidRequest;

import java.time.Duration;

import static com.acme.appdeploy.util.Arns.parseArnInstance;

public class UiAppServiceImpl implements IUiAppService {

    private final IConfigAppDao appDao;

    private final HealthChecks healthChecks = new HealthChecks();

    public UiAppServiceImpl(IConfigAppDao appDao) {
        this.appDao = appDao;
    }

    @Override
    public UiAppListResponse listApps(VoidRequest aRequest) {
        return new UiAppListResponse(
                appDao.listAllApps()
                        .stream()
                        .flatMap(it -> UiAppItemMapper.toAppItems(it, healthChecks))
                        .toList()
        );
    }

    private void debugSleep() {

        try {
            Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        throw new IllegalStateException("For debugging only");
    }

    @Override
    public UiAppViewResponse viewApp(ArnViewRequest aRequest) {
        debugSleep();

        ArnAppInstance        arnAppInstance = parseArnInstance(aRequest.getArn());
        AppInstanceFindResult appInstance    = new AppFinder(appDao.listAllApps()).findAppInstance(arnAppInstance);

        return UiAppViewResponse.builder()
                .appId        ( appInstance.getApp().getAppId()                )
                .envName      ( appInstance.getAppEnv().getEnvName()           )
                .instanceName ( appInstance.getAppInstance().getInstanceName() )
                .instanceArn  ( aRequest.getArn()                              )
                .build();
    }

}
