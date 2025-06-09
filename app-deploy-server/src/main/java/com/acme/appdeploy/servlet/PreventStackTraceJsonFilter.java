package com.acme.appdeploy.servlet;

import com.acme.appdeploy.servlet.messages.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static com.acme.appdeploy.servlet.ErrorResponses.writeErrorResponse;

public class PreventStackTraceJsonFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(PreventStackTraceJsonFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpServletRequest  httpRequest  = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorId    ( UUID.randomUUID().toString() )
                    .type       ( "GeneralError"               )
                    .title      ( e.getMessage()               )
                    .status     ( 500                          )
                    .detail(ErrorResponse.ErrorModelDetail.builder()
                                    .path   ( httpRequest.getRequestURI())
                                    .method ( httpRequest.getMethod())
                                    .build()
                    )
                    .build();

            writeErrorResponse(e, errorResponse, httpResponse);
        }
    }


    @Override
    public void destroy() {

    }
}
