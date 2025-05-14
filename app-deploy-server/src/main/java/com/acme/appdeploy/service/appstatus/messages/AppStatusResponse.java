package com.acme.appdeploy.service.appstatus.messages;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class AppStatusResponse {
    AppStatusResponseType type;
    String                errorMessage;
    String                appInstanceName;
    String                appVersion;
    String                hostname;
    int                   port;
    String                responseId;
    long                  responseEpoch;
    long                  uptimeMs;
}
