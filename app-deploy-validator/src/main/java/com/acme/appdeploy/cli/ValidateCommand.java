package com.acme.appdeploy.cli;

import com.acme.appdeploy.validator.ConfigValidator;
import com.acme.appdeploy.validator.ValidationResult;

import java.io.File;

/** {@code validate --config-dir <dir>} */
class ValidateCommand {

    int run(String[] args) {
        CliArgs opts = CliArgs.parse(args);
        if (opts.has("help")) {
            System.out.println("Usage: app-deploy-cli validate --config-dir <dir>");
            return 0;
        }

        File configDir = new File(opts.require("config-dir"));
        ValidationResult result = new ConfigValidator(configDir).validate();
        System.out.println(result.format());
        return result.hasErrors() ? 1 : 0;
    }
}
