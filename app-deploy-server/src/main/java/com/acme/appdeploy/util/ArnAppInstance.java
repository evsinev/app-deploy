package com.acme.appdeploy.util;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class ArnAppInstance {
    String appId;
    String envName;
    String instanceName;
}
