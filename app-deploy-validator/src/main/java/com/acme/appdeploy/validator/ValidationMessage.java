package com.acme.appdeploy.validator;

/**
 * A single validation finding.
 *
 * @param severity ERROR (config is broken) or WARNING (suspicious but tolerated)
 * @param file     config file the finding refers to, relative to the config dir (e.g. {@code apps/example-app.yaml})
 * @param path     YAML path inside the file (e.g. {@code envs[0].instances[1].deploy.dcAgentUrl.authRef}); may be empty
 * @param message  human readable description
 */
public record ValidationMessage(Severity severity, String file, String path, String message) {

    public static ValidationMessage error(String file, String path, String message) {
        return new ValidationMessage(Severity.ERROR, file, path, message);
    }

    public static ValidationMessage warning(String file, String path, String message) {
        return new ValidationMessage(Severity.WARNING, file, path, message);
    }

    @Override
    public String toString() {
        String location = path == null || path.isEmpty() ? file : file + "  " + path;
        return String.format("[%-7s] %s : %s", severity, location, message);
    }
}
