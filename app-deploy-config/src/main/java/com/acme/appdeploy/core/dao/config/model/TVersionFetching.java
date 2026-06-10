package com.acme.appdeploy.core.dao.config.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TVersionFetching {
    // optional; defaults to NGINX_DIR when absent
    VersionFetchingType              type;

    TVersionFetchingNginx            nginx;
    TVersionFetchingMavenMetadataXml mavenMetadataXml;
}
