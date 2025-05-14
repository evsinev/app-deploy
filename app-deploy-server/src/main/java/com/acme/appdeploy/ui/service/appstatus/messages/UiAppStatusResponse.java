package com.acme.appdeploy.ui.service.appstatus.messages;

import com.acme.appdeploy.service.appstatus.messages.AppStatusResponseType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class UiAppStatusResponse {
    AppStatusResponseType type;
    String                errorMessage;
    String                appInstanceName;
    String                appVersion;
    String                hostname;
    Integer               port;
    String                responseDateFormatted;
    String                uptimeFormatted;
}
