package com.acme.appdeploy.ui.service.app.impl;

import com.acme.appdeploy.dao.config.entity.TApp;
import com.acme.appdeploy.dao.config.entity.TAppEnv;
import com.acme.appdeploy.dao.config.entity.TAppInstance;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class AppInstanceFindResult {
    TApp         app;
    TAppEnv      appEnv;
    TAppInstance appInstance;
}
