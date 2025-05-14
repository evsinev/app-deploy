package com.acme.appdeploy.ui.service.version.messages;

import com.acme.appdeploy.ui.service.version.model.AppVersionItem;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class AvailableAppVersionsResponse {
    String               appId;
    List<AppVersionItem> appVersions;
}
