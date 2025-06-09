package com.acme.appdeploy;



import com.payneteasy.jetty.util.IJettyStartupParameters;
import com.payneteasy.startup.parameters.AStartupParameter;

import java.io.File;

public interface IStartupConfig extends IJettyStartupParameters {

    @AStartupParameter(
            name = "JETTY_PORT",
            value = "2301"
    )
    int getJettyPort();

    @Override
    @AStartupParameter(
            name = "JETTY_CONTEXT",
            value = "/app-deploy"
    )
    String getJettyContext();

    @AStartupParameter(name = "APP_DEPLOY_CONFIG", value = "./example-config")
    File configDir();

    @AStartupParameter(name = "APP_DEPLOY_DATABASE", value = "./target/database")
    File databaseDir();

    @AStartupParameter(name = "EXAMPLE_MOCK_ENABLED", value = "false")
    boolean isExampleMockEnabled();

}
