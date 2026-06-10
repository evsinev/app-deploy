# app-deploy

Modules:

- **app-deploy-config** — config model (entities) and YAML loaders, shared by the server and the CLI.
- **app-deploy-server** — the Jetty deploy server.
- **app-deploy-validator** — the `app-deploy-cli` tool: validate a config directory and deploy it.

## Config CLI (`app-deploy-cli`)

A small, dependency-light CLI for working with app-deploy config repositories
(`auths.yaml` + `apps/*.yaml`). Two independent subcommands: `validate` and `deploy`.

### Build

Requires JDK 21.

```bash
./mvnw -pl app-deploy-validator -am package -P validator-shaded
# -> app-deploy-validator/target/app-deploy-cli.jar
```

### validate

Checks a config directory and prints all findings (it never stops at the first error).
Exit code is `0` when there are no errors, `1` otherwise — suitable for CI.

```bash
java -jar app-deploy-cli.jar validate --config-dir ../config
```

What it checks:

- **structure / required fields** — auth fields per `authType`, `appId`, version-fetching and
  deploy blocks matching their `type`, non-empty `envs`/`instances`, `appStatus.url`, etc.
- **valid enum values** — `authType`, `versionFetching.type` (optional, defaults to
  `NGINX_DIR`), `deployType`, `appStatus.type`.
- **uniqueness** — `authId`, `appId`, and the `appId` + `envName` + `instanceName` triple
  (`envName` itself need not be unique).
- **referential integrity** — every `authRef` resolves to an `authId` in `auths.yaml`
  (an `appStatus` with `type: VERSION_TXT` does not require an `authRef`).
- **unknown keys** — typos like `fetchType` instead of `type`, which YAML→Gson parsing would
  otherwise silently ignore.
- **URL format** — `http(s)` scheme; `{{ APP_VERSION }}` templates are tolerated, and a missing
  `{{ APP_VERSION }}` in `artifact.artifactUrl` is reported as a warning.

### deploy

Zips the **contents** of a config directory (no top-level folder) and uploads it to a dc-agent
`zip-archive` endpoint as a binary `POST` with an `api-key` header. This replaces the old
`push-*.sh` scripts. `deploy` does **not** run validation — run `validate` first if you want that.

```bash
java -jar app-deploy-cli.jar deploy \
  --config-dir ../config \
  --path https://server-1.internal/dc-agent/zip-archive/app-deploy-config \
  --deploy-key 12345
```

### Typical CI flow

```bash
java -jar app-deploy-cli.jar validate --config-dir config
java -jar app-deploy-cli.jar deploy   --config-dir config --path "$DEPLOY_URL" --deploy-key "$DEPLOY_KEY"
```
