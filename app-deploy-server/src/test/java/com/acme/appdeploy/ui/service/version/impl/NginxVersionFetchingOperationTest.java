package com.acme.appdeploy.ui.service.version.impl;

import com.acme.appdeploy.ui.service.version.model.AppVersionItem;
import org.junit.Test;

import static com.acme.appdeploy.ui.service.version.impl.NginxVersionFetchingOperation.parseLine;
import static org.assertj.core.api.Assertions.assertThat;

public class NginxVersionFetchingOperationTest {

    @Test
    public void parse_line() {
        AppVersionItem version = parseLine(
                "<a href=\"app-name-1.0.4.jar\">app-name-1.0.4.jar</a>                          31-Jan-2025 21:06            34864854"
                , "app-name-"
                , ".jar"
        );
        assertThat(version.getAppVersion()).isEqualTo("1.0.4");
        assertThat(version.getTimestamp()).isEqualTo("31-Jan-2025 21:06");
        assertThat(version.getSize()).isEqualTo(34_864_854);
    }

}
