package com.acme.appdeploy.core.dao.config;

import com.acme.appdeploy.core.dao.config.entity.TAuth;

public interface IConfigAuthDao {

    TAuth findAuthById(String id);

}
