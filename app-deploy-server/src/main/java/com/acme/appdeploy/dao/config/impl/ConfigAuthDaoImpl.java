package com.acme.appdeploy.dao.config.impl;

import com.acme.appdeploy.dao.config.IConfigAuthDao;
import com.acme.appdeploy.dao.config.entity.TAuth;
import com.acme.appdeploy.dao.config.entity.TAuthStorage;
import com.payneteasy.yaml2json.YamlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ConfigAuthDaoImpl implements IConfigAuthDao {

    private static final Logger LOG = LoggerFactory.getLogger( ConfigAuthDaoImpl.class );

    private final YamlParser yamlParser = new YamlParser();

    private final File authsFile;

    public ConfigAuthDaoImpl(File authsFile) {
        this.authsFile = authsFile;
    }

    @Override
    public TAuth findAuthById(String id) {
        LOG.debug("Loading auths from {}", authsFile);
        TAuthStorage storage = yamlParser.parseFile(authsFile, TAuthStorage.class);

        return storage.getAuths()
                .stream()
                .filter(it -> it.getAuthId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find auth with id " + id));
    }
}
