package com.acme.appdeploy.ui.service.deploy.messages;

import com.acme.appdeploy.dao.deploylog.model.DeployStatus;
import com.acme.appdeploy.ui.service.deploy.model.DeployLogItem;
import com.acme.appdeploy.util.StatusIndicatorType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class DeployViewResponse {
    String              deployId;
    String              appId;
    String              newVersion;
    String              oldVersion;
    String              instanceName;
    String              instanceArn;
    String              instancePath;
    String              envName;
    List<DeployLogItem> logs;
    DeployStatus        status;
    StatusIndicatorType statusIndicator;
    String              statusErrorMessage;
}
