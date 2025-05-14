package com.acme.appdeploy.dao.deploylog.entity;

import com.acme.appdeploy.dao.deploylog.model.DeployStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder(toBuilder = true)
public class TDeploy {
    String       deployId;
    String       appId;
    String       envName;
    String       instanceName;
    String       newVersion;
    String       oldVersion;
    long         startedEpoch;
    long         endedEpoch;
    DeployStatus status;
    String       statusErrorMessage;
}
