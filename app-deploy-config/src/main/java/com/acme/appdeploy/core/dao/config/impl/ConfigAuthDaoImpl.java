package com.acme.appdeploy.core.dao.config.impl;

import com.acme.appdeploy.core.dao.config.IConfigAuthDao;
import com.acme.appdeploy.core.dao.config.entity.TAuth;
import com.acme.appdeploy.core.dao.config.entity.TAuthStorage;
import com.payneteasy.yaml2json.YamlParser;

import java.io.File;

public class ConfigAuthDaoImpl implements IConfigAuthDao {

    private final YamlParser yamlParser = new YamlParser();

    private final File authsFile;

    public ConfigAuthDaoImpl(File authsFile) {
        this.authsFile = authsFile;
    }

    @Override
    public TAuth findAuthById(String id) {
        TAuthStorage storage = yamlParser.parseFile(authsFile, TAuthStorage.class);

        return storage.getAuths()
                .stream()
                .filter(it -> it.getAuthId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find auth with id = '" + id + "'"));
    }
}
