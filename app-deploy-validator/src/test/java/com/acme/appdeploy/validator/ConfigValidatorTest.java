package com.acme.appdeploy.validator;

import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigValidatorTest {

    private ValidationResult validate(String configName) throws Exception {
        File dir = new File(getClass().getResource("/configs/" + configName).toURI());
        return new ConfigValidator(dir).validate();
    }

    private List<String> paths(ValidationResult result) {
        return result.messages().stream().map(ValidationMessage::path).toList();
    }

    @Test
    public void valid_config_has_no_errors() throws Exception {
        ValidationResult result = validate("valid");

        assertThat(result.hasErrors()).isFalse();
        assertThat(result.messages()).isEmpty();
    }

    @Test
    public void unknown_key_is_reported() throws Exception {
        ValidationResult result = validate("bad-unknown-key");

        assertThat(result.hasErrors()).isTrue();
        assertThat(paths(result)).contains("versionFetching.fetchType");
        assertThat(result.messages())
                .anyMatch(m -> m.path().equals("versionFetching.fetchType")
                        && m.message().contains("unknown key"));
    }

    @Test
    public void dangling_authRef_is_reported() throws Exception {
        ValidationResult result = validate("bad-authref");

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.messages())
                .anyMatch(m -> m.message().contains("does-not-exist")
                        && m.message().contains("not defined"));
    }

    @Test
    public void duplicate_appId_is_reported() throws Exception {
        ValidationResult result = validate("bad-duplicate-appid");

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.messages())
                .anyMatch(m -> m.path().equals("appId") && m.message().contains("duplicate appId"));
    }

    @Test
    public void duplicate_env_instance_pair_is_reported() throws Exception {
        ValidationResult result = validate("bad-duplicate-instance");

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.messages())
                .anyMatch(m -> m.message().contains("duplicate envName + instanceName"));
    }

    @Test
    public void repeated_envName_is_allowed() throws Exception {
        // the 'valid' fixture has app2 with two envs both named 'prod' (distinct instance names)
        ValidationResult result = validate("valid");

        assertThat(result.messages()).noneMatch(m -> m.message().contains("duplicate"));
    }

    @Test
    public void missing_type_defaults_to_nginx_without_error() throws Exception {
        // the 'valid' fixture has app2 with no type and an nginx block; it must validate clean
        ValidationResult result = validate("valid");

        assertThat(result.messages())
                .noneMatch(m -> m.path().startsWith("versionFetching"));
    }

    @Test
    public void version_txt_appStatus_does_not_require_authRef() throws Exception {
        // the 'valid' fixture has a VERSION_TXT instance with no authRef; it must not be flagged
        ValidationResult result = validate("valid");

        assertThat(result.messages())
                .noneMatch(m -> m.path().contains("appStatus.authRef"));
    }
}
