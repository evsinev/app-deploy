package com.acme.appdeploy.ui.service.app.messages;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class UiAppViewResponse {
    String appId;
    String envName;
    String instanceName;
    String instanceArn;
}
