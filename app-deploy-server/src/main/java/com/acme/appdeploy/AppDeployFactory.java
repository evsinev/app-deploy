package com.acme.appdeploy;


import com.acme.appdeploy.dao.config.IConfigAppDao;
import com.acme.appdeploy.dao.config.IConfigAuthDao;
import com.acme.appdeploy.dao.config.impl.ConfigAddDaoImpl;
import com.acme.appdeploy.dao.config.impl.ConfigAuthDaoImpl;
import com.acme.appdeploy.dao.deploylog.IDeployLogDao;
import com.acme.appdeploy.dao.deploylog.impl.DeployLogDaoImpl;
import com.acme.appdeploy.service.deploy.IDeployService;
import com.acme.appdeploy.service.deploy.impl.DeployServiceImpl;
import com.acme.appdeploy.service.http.IHttpService;
import com.acme.appdeploy.service.http.impl.HttpServiceImpl;
import com.acme.appdeploy.ui.service.app.IUiAppService;
import com.acme.appdeploy.ui.service.app.impl.UiAppServiceImpl;
import com.acme.appdeploy.ui.service.appstatus.IUiAppStatusService;
import com.acme.appdeploy.ui.service.appstatus.impl.UiAppStatusServiceImpl;
import com.acme.appdeploy.ui.service.deploy.IUiDeployService;
import com.acme.appdeploy.ui.service.deploy.impl.UiDeployServiceImpl;
import com.acme.appdeploy.ui.service.version.IAppVersionService;
import com.acme.appdeploy.ui.service.version.impl.AppVersionServiceImpl;
import com.payneteasy.mini.core.context.IServiceContext;
import com.payneteasy.mini.core.context.IServiceCreator;
import com.payneteasy.mini.core.context.ServiceContextImpl;

import java.io.File;

public class AppDeployFactory {

    private final IServiceContext context = new ServiceContextImpl();

    private final IStartupConfig config;

    public AppDeployFactory(IStartupConfig config) {
        this.config = config;
    }

    private <T> T singleton(Class<? super T> aClass, IServiceCreator<T> aCreator) {
        return context.singleton(aClass, aCreator);
    }

    public IConfigAppDao configAppDao() {
        return singleton(IConfigAppDao.class, () -> new ConfigAddDaoImpl(getConfigDirOrFile("apps")));
    }

    public IUiAppService uiAppService() {
        return singleton(IUiAppService.class, () -> new UiAppServiceImpl(configAppDao()));
    }

    public IConfigAuthDao configAuthDao() {
        return singleton(IConfigAuthDao.class, () -> new ConfigAuthDaoImpl(getConfigDirOrFile("auths.yaml")));
    }

    private File getConfigDirOrFile(String aDir) {
        return new File(config.configDir(), aDir);
    }

    private File getDatabaseDir(String aDir) {
        return new File(config.databaseDir(), aDir);
    }

    public IAppVersionService appVersionService() {
        return singleton(IAppVersionService.class, () -> new AppVersionServiceImpl(configAppDao(), configAuthDao()));
    }

    public IUiAppStatusService uiAppStatusService() {
        return singleton(IUiAppStatusService.class, () -> new UiAppStatusServiceImpl(configAppDao(), configAuthDao()));
    }

    private IDeployLogDao deployLogDao() {
        return singleton(IDeployLogDao.class, () -> new DeployLogDaoImpl(getDatabaseDir("deployments")));
    }

    public IUiDeployService deployService() {
        return singleton(IUiDeployService.class, () -> new UiDeployServiceImpl(deployRunService(), deployLogDao()));
    }

    private IDeployService deployRunService() {
        return singleton(IDeployService.class, () -> new DeployServiceImpl(deployLogDao(), configAppDao(), httpService(), uiAppStatusService()));
    }

    private IHttpService httpService() {
        return singleton(IHttpService.class, () -> new HttpServiceImpl(configAuthDao()));
    }
}
