package com.acme.appdeploy.deploy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Uploads a zipped config to a dc-agent zip-archive endpoint, reproducing the {@code curl}
 * call in the old {@code push-*.sh} scripts: a raw binary POST with an {@code api-key} header.
 */
public final class ConfigUploader {

    private ConfigUploader() {
    }

    public record Response(int status, String body) {
        public boolean isSuccess() {
            return status >= 200 && status < 300;
        }
    }

    public static Response upload(String url, byte[] zip, String deployKey) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("api-key", deployKey)
                .header("content-type", "application/zip")
                .POST(HttpRequest.BodyPublishers.ofByteArray(zip))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new Response(response.statusCode(), response.body());
    }
}
