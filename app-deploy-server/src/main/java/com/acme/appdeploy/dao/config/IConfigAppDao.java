package com.acme.appdeploy.dao.config;

import com.acme.appdeploy.dao.config.entity.TApp;

import java.util.List;
import java.util.Optional;

public interface IConfigAppDao {

     List<TApp> listAllApps();

     Optional<TApp> findAppById(String id);

}
