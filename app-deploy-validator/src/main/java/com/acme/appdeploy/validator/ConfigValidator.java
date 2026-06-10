package com.acme.appdeploy.validator;

import com.acme.appdeploy.core.dao.config.entity.*;
import com.acme.appdeploy.core.dao.config.model.*;
import com.acme.appdeploy.core.util.ListFiles;
import com.payneteasy.yaml2json.YamlParser;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates an app-deploy config directory: {@code auths.yaml} plus {@code apps/*.yaml}.
 * Collects every finding (never stops on the first error) and checks:
 * structure / required fields, valid enum values, uniqueness, referential integrity of
 * {@code authRef}, unknown keys (typos), and URL format.
 */
public class ConfigValidator {

    private static final String AUTHS_FILE = "auths.yaml";
    private static final String APPS_DIR   = "apps";

    private final File       configDir;
    private final YamlParser yamlParser = new YamlParser();

    public ConfigValidator(File configDir) {
        this.configDir = configDir;
    }

    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();

        if (!configDir.isDirectory()) {
            result.error(configDir.getPath(), "", "config directory does not exist");
            return result;
        }

        Set<String> authIds = validateAuths(result);
        validateApps(result, authIds);
        return result;
    }

    // ---- auths.yaml --------------------------------------------------------

    private Set<String> validateAuths(ValidationResult result) {
        Set<String> authIds = new HashSet<>();
        File        file    = new File(configDir, AUTHS_FILE);

        if (!file.isFile()) {
            result.error(AUTHS_FILE, "", "file not found");
            return authIds;
        }

        checkUnknownKeys(file, AUTHS_FILE, SchemaSpec.AUTHS, result);

        TAuthStorage storage;
        try {
            storage = yamlParser.parseFile(file, TAuthStorage.class);
        } catch (Exception e) {
            result.error(AUTHS_FILE, "", "cannot parse: " + rootMessage(e));
            return authIds;
        }

        List<TAuth> auths = storage == null ? null : storage.getAuths();
        if (auths == null || auths.isEmpty()) {
            result.error(AUTHS_FILE, "auths", "no auths defined");
            return authIds;
        }

        for (int i = 0; i < auths.size(); i++) {
            TAuth  auth = auths.get(i);
            String base = "auths[" + i + "]";

            String authId = auth.getAuthId();
            if (isBlank(authId)) {
                result.error(AUTHS_FILE, base + ".authId", "authId is required");
            } else if (!authIds.add(authId)) {
                result.error(AUTHS_FILE, base + ".authId", "duplicate authId '" + authId + "'");
            }

            AuthType type = auth.getAuthType();
            if (type == null) {
                result.error(AUTHS_FILE, base + ".authType", "authType is missing or has an invalid value");
                continue;
            }

            switch (type) {
                case AUTH_BASIC -> {
                    if (auth.getBasic() == null
                            || isBlank(auth.getBasic().getUsername())
                            || isBlank(auth.getBasic().getPassword())) {
                        result.error(AUTHS_FILE, base + ".basic", "AUTH_BASIC requires basic.username and basic.password");
                    }
                }
                case BEARER_TOKEN -> {
                    if (isBlank(auth.getBearerToken())) {
                        result.error(AUTHS_FILE, base + ".bearerToken", "BEARER_TOKEN requires bearerToken");
                    }
                }
                case API_KEY -> {
                    if (isBlank(auth.getApiKey())) {
                        result.error(AUTHS_FILE, base + ".apiKey", "API_KEY requires apiKey");
                    }
                }
                case AUTH_NONE -> { /* nothing required */ }
            }
        }

        return authIds;
    }

    // ---- apps/*.yaml -------------------------------------------------------

    private void validateApps(ValidationResult result, Set<String> authIds) {
        File appsDir = new File(configDir, APPS_DIR);
        if (!appsDir.isDirectory()) {
            result.error(APPS_DIR, "", "apps directory does not exist");
            return;
        }

        List<File> files = ListFiles.listSortedFiles(appsDir,
                f -> f.isFile() && f.getName().endsWith(".yaml"));
        if (files.isEmpty()) {
            result.warning(APPS_DIR, "", "no app files (*.yaml) found");
            return;
        }

        Set<String> appIds = new HashSet<>();
        for (File file : files) {
            validateApp(file, result, authIds, appIds);
        }
    }

    private void validateApp(File file, ValidationResult result, Set<String> authIds, Set<String> appIds) {
        String name = APPS_DIR + "/" + file.getName();

        checkUnknownKeys(file, name, SchemaSpec.APP, result);

        TApp app;
        try {
            app = yamlParser.parseFile(file, TApp.class);
        } catch (Exception e) {
            result.error(name, "", "cannot parse: " + rootMessage(e));
            return;
        }
        if (app == null) {
            result.error(name, "", "empty config");
            return;
        }

        if (isBlank(app.getAppId())) {
            result.error(name, "appId", "appId is required");
        } else if (!appIds.add(app.getAppId())) {
            result.error(name, "appId", "duplicate appId '" + app.getAppId() + "' (also defined in another file)");
        }

        validateVersionFetching(app.getVersionFetching(), name, result, authIds);
        validateArtifact(app.getArtifact(), name, result, authIds);
        validateEnvs(app.getEnvs(), name, result, authIds);
    }

    private void validateVersionFetching(TVersionFetching vf, String file, ValidationResult result, Set<String> authIds) {
        if (vf == null) {
            result.error(file, "versionFetching", "versionFetching is required");
            return;
        }

        // type is optional and defaults to NGINX_DIR when absent (null). A non-null but
        // unrecognized value is also parsed as null by Gson; we cannot tell the two apart here,
        // so an invalid type silently falls back to the NGINX_DIR branch and is then caught
        // by the missing-nginx-block / unknown-key checks.
        VersionFetchingType type = vf.getType() == null ? VersionFetchingType.NGINX_DIR : vf.getType();

        switch (type) {
            case MAVEN_METADATA_XML -> {
                if (vf.getMavenMetadataXml() == null) {
                    result.error(file, "versionFetching.mavenMetadataXml", "mavenMetadataXml block is required for MAVEN_METADATA_XML");
                } else {
                    checkUrl(vf.getMavenMetadataXml().getUrl(), file, "versionFetching.mavenMetadataXml.url", result, true);
                    checkAuthRef(vf.getMavenMetadataXml().getAuthRef(), file, "versionFetching.mavenMetadataXml.authRef", result, authIds, true);
                }
            }
            case NGINX_DIR -> {
                if (vf.getNginx() == null) {
                    result.error(file, "versionFetching.nginx", "nginx block is required for NGINX_DIR");
                } else {
                    checkUrl(vf.getNginx().getDirUrl(), file, "versionFetching.nginx.dirUrl", result, true);
                    checkAuthRef(vf.getNginx().getAuthRef(), file, "versionFetching.nginx.authRef", result, authIds, true);
                }
            }
        }
    }

    private void validateArtifact(TAppArtifact artifact, String file, ValidationResult result, Set<String> authIds) {
        if (artifact == null) {
            result.error(file, "artifact", "artifact is required");
            return;
        }
        String url = artifact.getArtifactUrl();
        checkUrl(url, file, "artifact.artifactUrl", result, true);
        if (!isBlank(url) && !url.contains("{{ APP_VERSION }}")) {
            result.warning(file, "artifact.artifactUrl", "does not contain the {{ APP_VERSION }} placeholder");
        }
        checkAuthRef(artifact.getAuthRef(), file, "artifact.authRef", result, authIds, true);
    }

    private void validateEnvs(List<TAppEnv> envs, String file, ValidationResult result, Set<String> authIds) {
        if (envs == null || envs.isEmpty()) {
            result.error(file, "envs", "at least one env is required");
            return;
        }

        // envName need not be unique; the unique key is the (envName, instanceName) pair within an app
        // (appId is fixed per file, so this is the appId + envName + instanceName triple).
        Set<String> envInstancePairs = new HashSet<>();
        for (int e = 0; e < envs.size(); e++) {
            TAppEnv env  = envs.get(e);
            String  base = "envs[" + e + "]";

            if (isBlank(env.getEnvName())) {
                result.error(file, base + ".envName", "envName is required");
            }

            List<TAppInstance> instances = env.getInstances();
            if (instances == null || instances.isEmpty()) {
                result.error(file, base + ".instances", "at least one instance is required");
                continue;
            }

            for (int i = 0; i < instances.size(); i++) {
                TAppInstance instance = instances.get(i);
                String       ibase    = base + ".instances[" + i + "]";

                if (isBlank(instance.getInstanceName())) {
                    result.error(file, ibase + ".instanceName", "instanceName is required");
                } else if (!envInstancePairs.add(env.getEnvName() + " " + instance.getInstanceName())) {
                    result.error(file, ibase + ".instanceName",
                            "duplicate envName + instanceName '" + env.getEnvName() + "' / '" + instance.getInstanceName() + "'");
                }

                validateDeploy(instance.getDeploy(), file, ibase + ".deploy", result, authIds);
                validateAppStatus(instance.getAppStatus(), file, ibase + ".appStatus", result, authIds);
            }
        }
    }

    private void validateDeploy(TAppDeploy deploy, String file, String path, ValidationResult result, Set<String> authIds) {
        if (deploy == null) {
            result.error(file, path, "deploy is required");
            return;
        }
        DeployType type = deploy.getDeployType();
        if (type == null) {
            result.error(file, path + ".deployType", "deployType is missing or has an invalid value (expected DEPLOY_DC_AGENT_URL)");
            return;
        }
        if (type == DeployType.DEPLOY_DC_AGENT_URL) {
            TAppDeployDcAgentUrl dc = deploy.getDcAgentUrl();
            if (dc == null) {
                result.error(file, path + ".dcAgentUrl", "dcAgentUrl block is required for DEPLOY_DC_AGENT_URL");
            } else {
                checkUrl(dc.getUrl(), file, path + ".dcAgentUrl.url", result, true);
                checkAuthRef(dc.getAuthRef(), file, path + ".dcAgentUrl.authRef", result, authIds, true);
            }
        }
    }

    private void validateAppStatus(TAppStatus appStatus, String file, String path, ValidationResult result, Set<String> authIds) {
        if (appStatus == null) {
            result.error(file, path, "appStatus is required");
            return;
        }
        checkUrl(appStatus.getUrl(), file, path + ".url", result, true);

        // VERSION_TXT is read without auth; any other type (including the default APP_STATUS) needs a bearer token.
        boolean authRequired = appStatus.getType() != AppStatusType.VERSION_TXT;
        checkAuthRef(appStatus.getAuthRef(), file, path + ".authRef", result, authIds, authRequired);
    }

    // ---- shared helpers ----------------------------------------------------

    private void checkAuthRef(String authRef, String file, String path, ValidationResult result, Set<String> authIds, boolean required) {
        if (isBlank(authRef)) {
            if (required) {
                result.error(file, path, "authRef is required");
            }
            return;
        }
        if (!authIds.contains(authRef)) {
            result.error(file, path, "authRef '" + authRef + "' is not defined in " + AUTHS_FILE);
        }
    }

    private void checkUrl(String url, String file, String path, ValidationResult result, boolean required) {
        if (isBlank(url)) {
            if (required) {
                result.error(file, path, "url is required");
            }
            return;
        }
        try {
            // urls may contain {{ ... }} templates (e.g. {{ APP_VERSION }}); substitute before parsing
            String probe = url.replaceAll("\\{\\{[^}]*}}", "TEMPLATE");
            URI uri = URI.create(probe);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equals("http") || scheme.equals("https"))) {
                result.error(file, path, "url must start with http:// or https:// : '" + url + "'");
            }
        } catch (IllegalArgumentException e) {
            result.error(file, path, "malformed url: '" + url + "'");
        }
    }

    private void checkUnknownKeys(File file, String name, SchemaSpec spec, ValidationResult result) {
        try {
            spec.checkUnknownKeys(RawYaml.load(file), "", name, result);
        } catch (Exception e) {
            // a parse failure is reported by the typed parsing step; nothing to add here
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String rootMessage(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
    }
}
