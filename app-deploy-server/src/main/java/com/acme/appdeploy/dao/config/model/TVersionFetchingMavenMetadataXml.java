package com.acme.appdeploy.dao.config.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TVersionFetchingMavenMetadataXml {
    String url;
    String prefix;
    String removePrefix;
    String removeSuffix;
    String authRef;
}
