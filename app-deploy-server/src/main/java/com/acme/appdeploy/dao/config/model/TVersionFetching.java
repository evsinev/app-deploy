package com.acme.appdeploy.dao.config.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TVersionFetching {
    VersionFetchingType   type;
    TNginxVersionFetching nginx;
    String                authRef;
}
