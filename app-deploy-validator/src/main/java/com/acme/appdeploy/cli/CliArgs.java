package com.acme.appdeploy.cli;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minimal command-line option parser (no dependencies). Accepts {@code --name value} and
 * {@code --name=value}; {@code -h}/{@code --help} and {@code -V}/{@code --version} are recognized
 * as valueless flags.
 */
final class CliArgs {

    /** Thrown on malformed input or a missing required option; the caller prints the message. */
    static final class CliException extends RuntimeException {
        CliException(String message) {
            super(message);
        }
    }

    private final Map<String, String> options = new LinkedHashMap<>();

    static CliArgs parse(String[] args) {
        CliArgs parsed = new CliArgs();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.equals("-h")) arg = "--help";
            if (arg.equals("-V")) arg = "--version";

            if (!arg.startsWith("--")) {
                throw new CliException("Unexpected argument: " + arg);
            }

            String key = arg.substring(2);
            String value = null;

            int eq = key.indexOf('=');
            if (eq >= 0) {
                value = key.substring(eq + 1);
                key   = key.substring(0, eq);
            } else if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                value = args[++i];
            }

            parsed.options.put(key, value);
        }
        return parsed;
    }

    boolean has(String key) {
        return options.containsKey(key);
    }

    String require(String key) {
        String value = options.get(key);
        if (value == null || value.isBlank()) {
            throw new CliException("Missing required option: --" + key);
        }
        return value;
    }
}
