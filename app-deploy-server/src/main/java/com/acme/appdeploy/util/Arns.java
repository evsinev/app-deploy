package com.acme.appdeploy.util;

import com.acme.appdeploy.dao.config.entity.TApp;
import com.acme.appdeploy.dao.config.entity.TAppEnv;
import com.acme.appdeploy.dao.config.entity.TAppInstance;

public class Arns {

    public static String instanceArn(TApp app, TAppEnv aEnv, TAppInstance instance) {
        return String.format("arn:instance:%s:%s:%s", app.getAppId(), aEnv.getEnvName(), instance.getInstanceName());
    }

    public static String instanceArn(String app, String aEnv, String instance) {
        return String.format("arn:instance:%s:%s:%s", app, aEnv, instance);
    }

    public static String envArn(TApp app, TAppEnv aEnv) {
        return String.format("arn:env:%s:%s", app.getAppId(), aEnv.getEnvName());
    }

    public static ArnAppInstance parseArnInstance(String aArn) {
        if (!aArn.startsWith("arn:instance:")) {
            throw new IllegalArgumentException("Invalid ARN: " + aArn);
        }

        String[] parts = aArn.split(":");
        return ArnAppInstance.builder()
                .appId        ( parts[2] )
                .envName      ( parts[3] )
                .instanceName ( parts[4] )
                .build();
    }
}
