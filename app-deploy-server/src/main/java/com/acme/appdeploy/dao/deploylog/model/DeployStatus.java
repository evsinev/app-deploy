package com.acme.appdeploy.dao.deploylog.model;

import com.acme.appdeploy.util.StatusIndicatorType;

import static com.acme.appdeploy.util.StatusIndicatorType.*;

public enum DeployStatus {
    SCHEDULED ( INDICATOR_PENDING ),
    RUNNING   ( INDICATOR_LOADING ),
    SUCCESS   ( INDICATOR_SUCCESS ),
    FAILED    ( INDICATOR_ERROR   ),
    ;

    private final StatusIndicatorType indicator;

    DeployStatus(StatusIndicatorType indicator) {
        this.indicator = indicator;
    }

    public StatusIndicatorType getIndicator() {
        return indicator;
    }
}
