package com.acme.appdeploy.service.appstatus.impl;


import com.acme.appdeploy.service.appstatus.IAppStatusClient;
import com.acme.appdeploy.service.appstatus.messages.AppStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppStatusClientImplExample {

    private static final Logger LOG = LoggerFactory.getLogger( AppStatusClientImplExample.class );

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: <url> <token>");
            System.exit(1);
        }


        String url = args[0];
        String token = args[1];

        IAppStatusClient client = new AppStatusClientImpl();
        AppStatusResponse appStatus = client.getAppStatus(url, token);
        LOG.info("appStatus {}", appStatus);
    }
}
