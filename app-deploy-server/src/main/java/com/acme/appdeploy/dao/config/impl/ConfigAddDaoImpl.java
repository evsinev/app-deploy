package com.acme.appdeploy.dao.config.impl;

import com.acme.appdeploy.dao.config.IConfigAppDao;
import com.acme.appdeploy.dao.config.entity.TApp;
import com.acme.appdeploy.util.SafeFiles;
import com.payneteasy.yaml2json.YamlParser;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class ConfigAddDaoImpl implements IConfigAppDao {

    private final File appsDir;

    private final YamlParser yamlParser = new YamlParser();

    public ConfigAddDaoImpl(File appsDir) {
        this.appsDir = appsDir;
    }

    @Override
    public List<TApp> listAllApps() {
        return SafeFiles.listSortedFiles(appsDir, file -> file.isFile() && file.getName().endsWith(".yaml"))
                .stream()
                .map(this::loadYaml)
                .toList();
    }

    @Override
    public Optional<TApp> findAppById(String id) {
        return listAllApps()
                .stream()
                .filter(it -> it.getAppId().equals(id))
                .findFirst();
    }

    private TApp loadYaml(File aFile) {
        return yamlParser.parseFile(aFile, TApp.class);
    }
}
