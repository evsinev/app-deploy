package com.acme.appdeploy.util;


import org.junit.Test;

import static com.acme.appdeploy.util.VersionNumbers.versionNumber;
import static com.acme.appdeploy.util.VersionNumbers.versionNumberDynamic;
import static org.assertj.core.api.Assertions.assertThat;

public class VersionNumbersTest {

    @Test
    public void test() {
        assertThat(versionNumber("1.2.3")).isEqualTo(1_002_003L);
        assertThat(versionNumber("10.20.30")).isEqualTo(10_020_030L);
        assertThat(versionNumber("15.26.37")).isEqualTo(15_026_037L);
        assertThat(versionNumber("999.999.999")).isEqualTo(999_999_999L);
    }

    @Test
    public void dynamic() {
        assertThat(versionNumberDynamic("1.2.3")).isEqualTo(1_0002_0003L);
        assertThat(versionNumberDynamic("10.20.30")).isEqualTo(10_0020_0030L);
        assertThat(versionNumberDynamic("15.26.37")).isEqualTo(15_0026_0037L);
        assertThat(versionNumberDynamic("999.999.999")).isEqualTo(999_0999_0999L);

        assertThat(versionNumberDynamic("1.2.3.4")).isEqualTo(1_0002_0003_0004L);
        assertThat(versionNumberDynamic("1.2.3.4-5")).isEqualTo(1_0002_0003_0004_0005L);
    }
}
