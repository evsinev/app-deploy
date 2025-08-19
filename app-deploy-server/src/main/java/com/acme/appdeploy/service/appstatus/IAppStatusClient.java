package com.acme.appdeploy.service.appstatus;

import com.acme.appdeploy.service.appstatus.messages.AppStatusResponse;

public interface IAppStatusClient {

    AppStatusResponse getAppStatus(String aUrl, String aBearerToken);

    AppStatusResponse getVersionTxt(String aUrl);

}
