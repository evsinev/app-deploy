package com.acme.appdeploy.mock;

import com.payneteasy.jetty.util.SafeHttpServlet;
import com.payneteasy.jetty.util.SafeServletRequest;
import com.payneteasy.jetty.util.SafeServletResponse;

public class MockAppStatusServlet extends SafeHttpServlet {

    @Override
    protected void doSafeGet(SafeServletRequest aRequest, SafeServletResponse aResponse) {
        aResponse.setContentType("application/json");
        aResponse.write(
                """
                {
                    "type": "OK",
                    "appInstanceName": "example-app",
                    "appVersion": "1.0.2",
                    "hostname": "host-1",
                    "port": 1234,
                    "responseId": "6aa2a15b-e51f-476d-804f-84a5278901b0",
                    "responseEpoch": 1747096918717,
                    "uptimeMs": 77699
                }
                """
        );
    }
}
