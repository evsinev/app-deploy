package com.acme.appdeploy.util;

import com.acme.appdeploy.dao.config.entity.TAuth;
import com.acme.appdeploy.dao.config.entity.TAuthBasic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

public class HttpAuths {

    private static final Logger LOG = LoggerFactory.getLogger( HttpAuths.class );

    public static HttpResponse<String> fetchHttpWithAuth(TAuth aAuth, String url, HttpClient httpClient) {
        HttpRequest httpRequest = addAuth(
                aAuth
                , HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(20))
        )
                .build();

        LOG.debug("Fetching versions from {}", url);

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot fetch " + url, e);
        }

        if (httpResponse.statusCode() != 200) {
            throw new IllegalArgumentException("Cannot fetch " + url + " - " + httpResponse.statusCode());
        }

        return httpResponse;
    }

    public static HttpRequest.Builder addAuth(TAuth aAuth, HttpRequest.Builder aRequest) {
        TAuthBasic basic                   = aAuth.getBasic();
        String     usernamePassword        = basic.getUsername() + ":" + basic.getPassword();
        String     encodedUsernamePassword = Base64.getEncoder().encodeToString(usernamePassword.getBytes());

        aRequest.header("Authorization", "Basic " + encodedUsernamePassword);

        return aRequest;
    }

}
