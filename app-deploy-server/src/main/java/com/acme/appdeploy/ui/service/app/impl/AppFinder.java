package com.acme.appdeploy.ui.service.app.impl;

import com.acme.appdeploy.dao.config.entity.TApp;
import com.acme.appdeploy.dao.config.entity.TAppEnv;
import com.acme.appdeploy.dao.config.entity.TAppInstance;
import com.acme.appdeploy.util.ArnAppInstance;

import javax.annotation.Nonnull;
import java.util.List;

public class AppFinder {

    private final List<TApp> apps;

    private record InstanceWithEnv(TAppInstance instance, TAppEnv env) {}

    public AppFinder(List<TApp> apps) {
        this.apps = apps;
    }

    @Nonnull
    public AppInstanceFindResult findAppInstance(ArnAppInstance arnAppInstance) {
        TApp app = findApp(arnAppInstance.getAppId());
        InstanceWithEnv instanceWithEnv = findInstance(app, arnAppInstance.getEnvName(), arnAppInstance.getInstanceName());
        return AppInstanceFindResult.builder()
                .app(app)
                .appEnv(instanceWithEnv.env)
                .appInstance(instanceWithEnv.instance)
                .build();
    }

    private InstanceWithEnv findInstance(TApp aApp, String envName, String instanceName) {
        return aApp.getEnvs()
                .stream()
                .flatMap(env -> env.getInstances()
                        .stream()
                        .map(instance -> new InstanceWithEnv(instance, env))
                )
                .filter(it -> it.instance.getInstanceName().equals(instanceName) && it.env.getEnvName().equals(envName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Instance not found: app = " + aApp.getAppId() + ", env = " + envName + ", instance = " + instanceName));
    }

    private TAppEnv findEnv(TApp app, String envName) {
        return app.getEnvs()
                .stream()
                .filter(env -> env.getEnvName().equals(envName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Env not found: " + envName + " in app " + app.getAppId()));
    }

    private TApp findApp(String appId) {
        return apps.stream()
                .filter(app -> app.getAppId().equals(appId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("App not found: " + appId));
    }
}
