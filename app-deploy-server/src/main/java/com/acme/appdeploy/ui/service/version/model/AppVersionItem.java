package com.acme.appdeploy.ui.service.version.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class AppVersionItem {
    String appVersion;
    long   appVersionNumber;
    String timestamp;
    long   size;
    String sizeFormatted;
}
