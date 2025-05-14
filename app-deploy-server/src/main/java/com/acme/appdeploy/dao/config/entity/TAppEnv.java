package com.acme.appdeploy.dao.config.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TAppEnv {
    String             envName;
    List<TAppInstance> instances;
    String             healthCheckUrl;
}
