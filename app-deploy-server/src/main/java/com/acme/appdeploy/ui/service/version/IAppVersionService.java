package com.acme.appdeploy.ui.service.version;

import com.acme.appdeploy.ui.service.version.messages.AppViewRequest;
import com.acme.appdeploy.ui.service.version.messages.AvailableAppVersionsResponse;

public interface IAppVersionService {

    AvailableAppVersionsResponse listAvailableAppVersions(AppViewRequest aRequest);
}
