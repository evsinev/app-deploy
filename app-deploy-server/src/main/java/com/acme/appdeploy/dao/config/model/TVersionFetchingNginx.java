package com.acme.appdeploy.dao.config.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TVersionFetchingNginx {
    String dirUrl;
    String prefix;
    String suffix;
    String authRef;
}
