package com.acme.appdeploy.util;

import com.acme.appdeploy.dao.config.entity.TApp;
import com.acme.appdeploy.dao.config.entity.TAppEnv;
import com.acme.appdeploy.dao.config.entity.TAppInstance;

public class InstancePaths {

    public static String instancePath(String aAppId, String aEnvName, String aInstanceName) {
        return String.format("%s / %s / %s", aEnvName, aAppId, aInstanceName);
    }

    public static String instancePath(TApp app, TAppEnv aEnv, TAppInstance instance) {
        return String.format("%s / %s / %s", aEnv.getEnvName(), app.getAppId(), instance.getInstanceName());
    }

}
