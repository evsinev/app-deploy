package com.acme.appdeploy.ui.service.appstatus;

import com.acme.appdeploy.ui.service.app.model.ArnViewRequest;
import com.acme.appdeploy.ui.service.appstatus.messages.UiAppStatusResponse;

public interface IUiAppStatusService {

    UiAppStatusResponse getInstanceStatus(ArnViewRequest aRequest);

}
