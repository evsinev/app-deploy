package com.acme.appdeploy.util;

import com.google.gson.annotations.SerializedName;

public enum StatusIndicatorType {
    @SerializedName( "error"       ) INDICATOR_ERROR,
    @SerializedName( "in-progress" ) INDICATOR_IN_PROGRESS,
    @SerializedName( "stopped"     ) INDICATOR_STOPPED,
    @SerializedName( "loading"     ) INDICATOR_LOADING,
    @SerializedName( "pending"     ) INDICATOR_PENDING,
    @SerializedName( "success"     ) INDICATOR_SUCCESS,
    @SerializedName( "warning"     ) INDICATOR_WARNING,
    @SerializedName( "info"        ) INDICATOR_INFO,
    ;
}
