package com.acme.appdeploy.util;


import org.junit.Test;

import static com.acme.appdeploy.util.SizeFormatter.formatSize;
import static org.assertj.core.api.Assertions.assertThat;

public class SizeFormatterTest {

    @Test
    public void test() {
        assertThat(formatSize(1023)).isEqualTo("1023 bytes");
        assertThat(formatSize(1024)).isEqualTo("1 KiB");
        assertThat(formatSize(34864854)).isEqualTo("33 MiB");
    }
}
