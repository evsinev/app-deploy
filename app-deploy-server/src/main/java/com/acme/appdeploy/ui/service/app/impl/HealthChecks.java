package com.acme.appdeploy.ui.service.app.impl;

import com.acme.appdeploy.ui.service.app.model.HealthCheckResult;
import com.acme.appdeploy.ui.service.app.model.HealthCheckResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static com.payneteasy.jetty.util.Strings.isEmpty;

public class HealthChecks {

    private static final Logger LOG = LoggerFactory.getLogger( HealthChecks.class );

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public HealthCheckResult healthCheck(String aUrl) {
        if (isEmpty(aUrl)) {
            return HealthCheckResult.builder()
                    .type(HealthCheckResultType.NO_HEALTH_CHECK_URL)
                    .errorMessage("No health check URL provided")
                    .build();
        }

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(aUrl))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            LOG.error("Cannot perform health check for url {}", aUrl, e);
            return HealthCheckResult.builder()
                    .type(HealthCheckResultType.ERROR)
                    .errorMessage(e.getMessage())
                    .build();
        }

        if (httpResponse.statusCode() != 200) {
            return HealthCheckResult.builder()
                    .type(HealthCheckResultType.ERROR)
                    .errorMessage("Health check returned " + httpResponse.statusCode())
                    .build();
        }

        return HealthCheckResult.builder()
                .type(HealthCheckResultType.OK)
                .build();
    }
}
