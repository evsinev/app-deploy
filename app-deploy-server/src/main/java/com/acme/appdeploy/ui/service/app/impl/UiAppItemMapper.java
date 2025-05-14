package com.acme.appdeploy.ui.service.app.impl;

import com.acme.appdeploy.dao.config.entity.TApp;
import com.acme.appdeploy.dao.config.entity.TAppEnv;
import com.acme.appdeploy.dao.config.entity.TAppInstance;
import com.acme.appdeploy.ui.service.app.model.UiAppItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.acme.appdeploy.util.Arns.envArn;
import static com.acme.appdeploy.util.Arns.instanceArn;
import static com.acme.appdeploy.util.InstancePaths.instancePath;

public class UiAppItemMapper {


    public static Stream<UiAppItem> toAppItems(TApp app, HealthChecks healthChecks) {
        List<UiAppItem> items = new ArrayList<>();
        for (TAppEnv env : app.getEnvs()) {
            for (TAppInstance instance : env.getInstances()) {
                items.add(
                        UiAppItem.builder()
                                .appId          ( app.getAppId())
                                .envName        ( env.getEnvName())
                                .envArn         ( envArn(app, env))
                                .instanceName   ( instance.getInstanceName())
                                .instanceArn    ( instanceArn(app, env, instance))
                                .instancePath   ( instancePath(app, env, instance))
                                .runningVersion ( "unknown" )
                                .build()
                );
            }
        }
        return items.stream();
    }

}
