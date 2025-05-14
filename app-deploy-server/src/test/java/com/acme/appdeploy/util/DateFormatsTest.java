package com.acme.appdeploy.util;

import org.junit.Test;

import java.time.ZoneOffset;

import static com.acme.appdeploy.util.DateFormats.formatEpoch;
import static org.assertj.core.api.Assertions.assertThat;


public class DateFormatsTest {

    @Test
    public void format_epoch() {
        assertThat(formatEpoch(1747080860101L, ZoneOffset.UTC)).isEqualTo("2025-05-12 20:14:20.101 Z");
    }
}
