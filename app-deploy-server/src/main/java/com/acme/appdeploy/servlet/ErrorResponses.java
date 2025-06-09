package com.acme.appdeploy.servlet;

import com.acme.appdeploy.servlet.messages.ErrorResponse;
import com.acme.appdeploy.util.Gsons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorResponses {
    private static final Logger LOG = LoggerFactory.getLogger( ErrorResponses.class );

    public static void writeErrorResponse(Exception e, ErrorResponse errorResponse, HttpServletResponse httpResponse) {
        String json = Gsons.toPrettyJson(errorResponse);
        LOG.error("{}: Error while processing trace {}", errorResponse.getErrorId(), json, e);

        try {
            httpResponse.setStatus(errorResponse.getStatus());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(json);
        } catch (IOException ioException) {
            LOG.error("{}: Cannot write error", errorResponse.getErrorId(), ioException);
        }
    }

}
