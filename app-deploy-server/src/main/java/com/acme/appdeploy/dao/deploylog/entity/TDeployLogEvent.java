package com.acme.appdeploy.dao.deploylog.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TDeployLogEvent {
    String deployId;
    long   epoch;
    String level;
    String message;
}
