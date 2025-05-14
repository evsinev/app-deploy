package com.acme.appdeploy.ui.service.app.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class UiAppItem {
    String                appId;
    String                envName;
    String                envArn;
    String                instanceName;
    String                instanceArn;
    String                instancePath;
    String                runningVersion;
    HealthCheckResult     envHealthCheckResult;
}
