package com.acme.appdeploy.mock;

import com.payneteasy.jetty.util.SafeHttpServlet;
import com.payneteasy.jetty.util.SafeServletRequest;
import com.payneteasy.jetty.util.SafeServletResponse;

public class MockNginxServlet extends SafeHttpServlet {

    @Override
    protected void doSafeGet(SafeServletRequest aRequest, SafeServletResponse aResponse) {
        aResponse.setContentType("text/plain");
        aResponse.write(
            """
            <html>
            <head><title>Index of /app-deploy/mock/nginx/</title></head>
            <body bgcolor="white">
            <h1>Index of /app-deploy/mock/nginx/</h1><hr><pre><a href="../">../</a>
            <a href="example-app-1.0.1.jar">example-app-1.0.1.jar</a>                             11-Nov-2024 22:23            18833201
            <a href="example-app-1.0.2.jar">example-app-1.0.2.jar</a>                             12-Jan-2025 01:34            20513072
            <a href="example-app-1.0.3.jar">example-app-1.0.3.jar</a>                             16-Jan-2025 01:03            30513393
            <a href="example-app-1.0.4.jar">example-app-1.0.4.jar</a>                             17-Jan-2025 21:08            40672894
            <a href="example-app-1.0.5.jar">example-app-1.0.5.jar</a>                             01-May-2025 00:45            50900585
            <a href="example-app-1.0.6.jar">example-app-1.0.6.jar</a>                             02-May-2025 01:02            60900596
            <a href="example-app-1.0.7.jar">example-app-1.0.7.jar</a>                             03-May-2025 02:14            70900607
            </pre><hr></body>
            </html>
            """
        );
    }
}
