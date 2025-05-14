package com.acme.appdeploy.service.appstatus.impl;

import com.acme.appdeploy.service.appstatus.IAppStatusClient;
import com.acme.appdeploy.service.appstatus.messages.AppStatusResponse;
import com.acme.appdeploy.service.appstatus.messages.AppStatusResponseType;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class AppStatusClientImpl implements IAppStatusClient {

    private static final Logger LOG = LoggerFactory.getLogger( AppStatusClientImpl.class );

    private final Gson gson = new Gson();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    @Override
    public AppStatusResponse getAppStatus(String aUrl, String aBearerToken) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(aUrl))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + aBearerToken)
                .build();

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            LOG.error("Cannot perform health check for url {}", aUrl, e);
            return AppStatusResponse.builder()
                    .type(AppStatusResponseType.ERROR)
                    .errorMessage(e.getMessage())
                    .build();
        }

        if (httpResponse.statusCode() != 200 && httpResponse.statusCode() != AppStatusResponseType.NOT_MATCHED.httpStatus()) {
            return AppStatusResponse.builder()
                    .type(AppStatusResponseType.ERROR)
                    .errorMessage("Wrong status code: " + httpResponse.statusCode())
                    .build();
        }

        return gson.fromJson(httpResponse.body(), AppStatusResponse.class);
    }
}
