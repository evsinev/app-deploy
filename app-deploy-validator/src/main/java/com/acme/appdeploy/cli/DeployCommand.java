package com.acme.appdeploy.cli;

import com.acme.appdeploy.deploy.ConfigUploader;
import com.acme.appdeploy.deploy.ConfigZipper;

import java.io.File;

/** {@code deploy --config-dir <dir> --path <url> --deploy-key <key>} */
class DeployCommand {

    int run(String[] args) {
        CliArgs opts = CliArgs.parse(args);
        if (opts.has("help")) {
            System.out.println("Usage: app-deploy-cli deploy --config-dir <dir> --path <url> --deploy-key <key>");
            return 0;
        }

        File   configDir = new File(opts.require("config-dir"));
        String path      = opts.require("path");
        String deployKey = opts.require("deploy-key");

        if (!configDir.isDirectory()) {
            System.err.println("Config directory does not exist: " + configDir);
            return 1;
        }

        try {
            byte[] zip = ConfigZipper.zipContents(configDir);
            System.out.printf("Zipped %s (%d bytes), uploading to %s ...%n", configDir, zip.length, path);

            ConfigUploader.Response response = ConfigUploader.upload(path, zip, deployKey);
            if (response.isSuccess()) {
                System.out.println("Upload OK (HTTP " + response.status() + ")");
                return 0;
            }

            System.err.println("Upload FAILED (HTTP " + response.status() + ")");
            if (response.body() != null && !response.body().isBlank()) {
                System.err.println(response.body());
            }
            return 1;
        } catch (Exception e) {
            System.err.println("Deploy failed: " + e.getMessage());
            return 1;
        }
    }
}
