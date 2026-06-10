package com.acme.appdeploy.deploy;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigZipperTest {

    @Test
    public void zips_contents_with_relative_entries() throws Exception {
        File dir = new File(getClass().getResource("/configs/valid").toURI());

        byte[] zip = ConfigZipper.zipContents(dir);

        List<String> entries = new ArrayList<>();
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(zip))) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                entries.add(entry.getName());
            }
        }

        // entries are relative to the config dir, no top level folder
        assertThat(entries).contains("auths.yaml", "apps/app1.yaml");
        assertThat(entries).noneMatch(name -> name.startsWith("valid/"));
    }
}
