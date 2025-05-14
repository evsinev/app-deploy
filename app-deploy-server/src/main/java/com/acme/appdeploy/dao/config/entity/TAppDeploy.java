package com.acme.appdeploy.dao.config.entity;

import com.acme.appdeploy.dao.config.model.DeployType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TAppDeploy {
    DeployType           deployType;
    TAppDeployDcAgentUrl dcAgentUrl;
}
