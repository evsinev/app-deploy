package com.acme.appdeploy.ui.service.deploy.model;

import com.acme.appdeploy.dao.deploylog.model.DeployStatus;
import com.acme.appdeploy.util.StatusIndicatorType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class DeployListItem {
    String              deployId;
    String              appId;
    String              envName;
    String              instanceName;
    String              instanceArn;
    String              newVersion;
    String              oldVersion;
    DeployStatus        status;
    StatusIndicatorType statusIndicator;
    String              statusErrorMessage;
}
