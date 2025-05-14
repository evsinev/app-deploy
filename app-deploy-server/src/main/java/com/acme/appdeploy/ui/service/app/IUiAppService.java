package com.acme.appdeploy.ui.service.app;

import com.acme.appdeploy.ui.service.app.messages.UiAppListResponse;
import com.acme.appdeploy.ui.service.app.messages.UiAppViewResponse;
import com.acme.appdeploy.ui.service.app.model.ArnViewRequest;
import com.payneteasy.apiservlet.VoidRequest;

public interface IUiAppService {

    UiAppListResponse listApps(VoidRequest aRequest);

    UiAppViewResponse viewApp(ArnViewRequest aRequest);
}
