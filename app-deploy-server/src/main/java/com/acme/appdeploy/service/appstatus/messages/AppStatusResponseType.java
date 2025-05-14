package com.acme.appdeploy.service.appstatus.messages;

import static javax.servlet.http.HttpServletResponse.*;

public enum AppStatusResponseType {
    OK          ( SC_OK ),
    NOT_MATCHED ( SC_PRECONDITION_FAILED ),
    ERROR       ( SC_INTERNAL_SERVER_ERROR),
    ;

    private final int httpStatus;

    AppStatusResponseType(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int httpStatus() {
        return httpStatus;
    }
}
