package com.acme.appdeploy.util;


import org.junit.Test;

import static com.acme.appdeploy.util.VersionNumbers.versionNumber;
import static org.assertj.core.api.Assertions.assertThat;

public class VersionNumbersTest {

    @Test
    public void test() {
        assertThat(versionNumber("1.2.3")).isEqualTo(1_002_003L);
        assertThat(versionNumber("10.20.30")).isEqualTo(10_020_030L);
        assertThat(versionNumber("15.26.37")).isEqualTo(15_026_037L);
        assertThat(versionNumber("999.999.999")).isEqualTo(999_999_999L);
    }

}
