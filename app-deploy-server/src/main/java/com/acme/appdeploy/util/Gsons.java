package com.acme.appdeploy.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Gsons {

    private static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static String toPrettyJson(Object aObj) {
        return GSON_PRETTY.toJson(aObj);
    }
}
