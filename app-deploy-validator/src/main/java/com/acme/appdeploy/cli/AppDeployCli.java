package com.acme.appdeploy.cli;

import com.acme.appdeploy.cli.CliArgs.CliException;

import java.util.Arrays;

/**
 * Entry point for the app-deploy CLI (no external CLI library).
 *
 * <pre>
 *   java -jar app-deploy-cli.jar validate --config-dir ../config
 *   java -jar app-deploy-cli.jar deploy   --config-dir ../config --path https://host/dc-agent/zip-archive/app-deploy-config --deploy-key 12345
 * </pre>
 */
public class AppDeployCli {

    public static void main(String[] args) {
        System.exit(run(args));
    }

    static int run(String[] args) {
        if (args.length == 0) {
            printUsage(System.err);
            return 2;
        }

        String command = args[0];
        if (command.equals("-h") || command.equals("--help")) {
            printUsage(System.out);
            return 0;
        }
        if (command.equals("-V") || command.equals("--version")) {
            System.out.println("app-deploy-cli " + version());
            return 0;
        }

        String[] rest = Arrays.copyOfRange(args, 1, args.length);
        try {
            return switch (command) {
                case "validate" -> new ValidateCommand().run(rest);
                case "deploy"   -> new DeployCommand().run(rest);
                default -> {
                    System.err.println("Unknown command: " + command);
                    printUsage(System.err);
                    yield 2;
                }
            };
        } catch (CliException e) {
            System.err.println(e.getMessage());
            return 2;
        }
    }

    private static void printUsage(java.io.PrintStream out) {
        out.println("""
                Usage: app-deploy-cli <command> [options]

                Commands:
                  validate --config-dir <dir>
                      Validate an app-deploy config directory (auths.yaml + apps/*.yaml).

                  deploy --config-dir <dir> --path <url> --deploy-key <key>
                      Zip a config directory and upload it to a dc-agent zip-archive endpoint.

                Options:
                  -h, --help      Show this help message and exit.
                  -V, --version   Print version information and exit.""");
    }

    private static String version() {
        String version = AppDeployCli.class.getPackage().getImplementationVersion();
        return version == null ? "(dev)" : version;
    }
}
