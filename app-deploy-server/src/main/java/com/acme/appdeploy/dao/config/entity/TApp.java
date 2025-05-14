package com.acme.appdeploy.dao.config.entity;

import com.acme.appdeploy.dao.config.model.TVersionFetching;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TApp {
    String           appId;
    List<TAppEnv>    envs;
    TVersionFetching versionFetching;
    TAppArtifact     artifact;
}
