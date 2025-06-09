package com.acme.appdeploy;


import com.acme.appdeploy.mock.MockAppStatusServlet;
import com.acme.appdeploy.mock.MockNginxServlet;
import com.acme.appdeploy.ui.service.app.IUiAppService;
import com.acme.appdeploy.ui.service.app.model.ArnViewRequest;
import com.acme.appdeploy.ui.service.appstatus.IUiAppStatusService;
import com.acme.appdeploy.ui.service.deploy.IUiDeployService;
import com.acme.appdeploy.ui.service.deploy.messages.DeployListRequest;
import com.acme.appdeploy.ui.service.deploy.messages.DeployViewRequest;
import com.acme.appdeploy.ui.service.deploy.messages.RunDeployRequest;
import com.acme.appdeploy.ui.service.version.IAppVersionService;
import com.acme.appdeploy.ui.service.version.messages.AppViewRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.apiservlet.GsonJettyContextHandler;
import com.payneteasy.apiservlet.VoidRequest;
import com.payneteasy.jetty.util.*;
import com.payneteasy.mini.core.app.AppContext;
import com.payneteasy.mini.core.app.AppRunner;
import com.payneteasy.mini.core.error.handler.ApiExceptionHandler;
import com.payneteasy.mini.core.error.handler.ApiRequestValidator;
import org.eclipse.jetty.ee8.servlet.ServletContextHandler;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class AppDeployApplication {

    public static void main(String[] args) {
        AppRunner.runApp(args, AppDeployApplication::run);
    }

    private static void run(AppContext aContext) {
        IStartupConfig    config           = getStartupParameters(IStartupConfig.class);

        AppDeployFactory factory = new AppDeployFactory(config);

        JettyServerBuilder jettyBuilder = new JettyServerBuilder()
                .startupParameters(config)
                .contextOption(JettyContextOption.SESSIONS)

                .filter("/*", new PreventStackTraceFilter())

                .servlet("/health", new HealthServlet())

                .contextListener(servletContextHandler -> configureContext(servletContextHandler, config, factory));

        if (config.isExampleMockEnabled()) {
            jettyBuilder.servlet("/mock/nginx", new MockNginxServlet());
            jettyBuilder.servlet("/mock/app-status/*", new MockAppStatusServlet());
        }

        JettyServer jetty = jettyBuilder.build();
        jetty.startJetty();
    }

    private static void configureContext(ServletContextHandler servletContextHandler, IStartupConfig config, AppDeployFactory aFactory) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        GsonJettyContextHandler gsonHandler = new GsonJettyContextHandler(
                servletContextHandler
                , gson
                , new ApiExceptionHandler()
                , new ApiRequestValidator()
        );

        IUiAppService app = aFactory.uiAppService();
        gsonHandler.addApi("/api/app/list/*", app::listApps, VoidRequest.class);
        gsonHandler.addApi("/api/app/view/*", app::viewApp, ArnViewRequest.class);

        IAppVersionService versions = aFactory.appVersionService();
        gsonHandler.addApi("/api/version/list-available/*", versions::listAvailableAppVersions, AppViewRequest.class);

        IUiAppStatusService status = aFactory.uiAppStatusService();
        gsonHandler.addApi("/api/app-status/instance/*", status::getInstanceStatus, ArnViewRequest.class);

        IUiDeployService deploy = aFactory.deployService();
        gsonHandler.addApi("/api/deploy/run-deploy/*", deploy::runDeploy, RunDeployRequest.class);
        gsonHandler.addApi("/api/deploy/view-deploy/*", deploy::viewDeploy, DeployViewRequest.class);
        gsonHandler.addApi("/api/deploy/deploy-list/*", deploy::listRecentDeploys, DeployListRequest.class);
    }

}
