package com.acme.appdeploy.core.dao.config.entity;

import com.acme.appdeploy.core.dao.config.model.TAppStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TAppInstance {
    String     instanceName;
    TAppDeploy deploy;
    TAppStatus appStatus;
}
