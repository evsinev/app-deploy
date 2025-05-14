package com.acme.appdeploy.service.deploy.impl;

import com.acme.appdeploy.dao.config.entity.*;
import com.acme.appdeploy.dao.deploylog.model.DeployStatus;
import com.acme.appdeploy.service.http.IHttpService;
import com.acme.appdeploy.util.SafeFiles;
import com.acme.appdeploy.util.SizeFormatter;
import org.slf4j.MDC;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DeployTask implements Runnable {

    private final TApp           app;
    private final TAppEnv        env;
    private final TAppInstance   instance;
    private final IDeployContext context;
    private final IHttpService   httpService;
    private final HttpClient     httpClient;
    private final String         appVersion;
    private final String         deployId;

    public DeployTask(TApp app, TAppEnv env, TAppInstance instance, IDeployContext context, IHttpService httpService, HttpClient httpClient, String appVersion, String deployId) {
        this.app         = app;
        this.env         = env;
        this.instance    = instance;
        this.context     = context;
        this.httpService = httpService;
        this.httpClient  = httpClient;
        this.appVersion  = appVersion;
        this.deployId    = deployId;
    }

    @Override
    public void run() {
        MDC.put("deployId", deployId);
        context.updateStatus(DeployStatus.RUNNING);
        try {
            File artifactFile = downloadArtifact();
            try {
                context.debug("Artifact downloaded to {}", artifactFile.getAbsolutePath());
                uploadArtifact(artifactFile);
            } finally {
                SafeFiles.deleteFileWithWarning(artifactFile, "Temp artifact");
            }
            context.updateStatus(DeployStatus.SUCCESS);
        } catch (Exception e) {
            context.error("Error: {}", e.getMessage(), e);
            context.updateStatusWithMessage(DeployStatus.FAILED, e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    private void uploadArtifact(File artifactFile) {
        TAppDeployDcAgentUrl dcAgent = instance.getDeploy().getDcAgentUrl();
        context.debug("Uploading artifact to {}", dcAgent.getUrl());

        HttpRequest.BodyPublisher bodyPublisher;
        try {
            bodyPublisher = HttpRequest.BodyPublishers.ofFile(artifactFile.toPath());
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create body from file " + artifactFile.getAbsolutePath(), e);
        }

        HttpRequest httpRequest = httpService.newHttpRquestBuilder(dcAgent.getAuthRef())
                .POST    ( bodyPublisher                )
                .uri     ( URI.create(dcAgent.getUrl()) )
                .timeout ( Duration.ofSeconds(100)      )
                .build();

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString(UTF_8));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot upload artifact to " + dcAgent.getUrl(), e);
        }

        if (httpResponse.statusCode() != 200) {
            throw new IllegalArgumentException("Wrong status code: " + httpResponse.statusCode() + "\n" + httpResponse.body());
        }

        context.debug("DcAgent response:\n{}", httpResponse.body().replace("Last log lines", "Last log lines\n\n"));
    }

    private File downloadArtifact() {
        TAppArtifact artifact    = app.getArtifact();
        String       artifactUrl = artifact.getArtifactUrl().replace("{{ APP_VERSION }}", appVersion);
        context.debug("Fetching artifact from {}", artifactUrl);

        HttpRequest request = httpService.newHttpRquestBuilder(artifact.getAuthRef())
                .uri(URI.create(artifactUrl))
                .GET()
                .build();

        File file;
        try {
            file = Files.createTempFile(app.getAppId() + "-", "-" + appVersion).toFile();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot create temp file", e);
        }

        HttpResponse<Path> httpResponse;
        try {
            httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofFile(file.toPath()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot download artifact from " + artifactUrl, e);
        }

        if (httpResponse.statusCode() != 200) {
            throw new IllegalArgumentException("Wrong status code: " + httpResponse.statusCode());
        }

        context.debug("Artifact size is {}", SizeFormatter.formatSize(file.length()));
        return file;
    }
}
