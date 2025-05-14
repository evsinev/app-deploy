package com.acme.appdeploy.dao.config;

import com.acme.appdeploy.dao.config.entity.TAuth;

public interface IConfigAuthDao {

    TAuth findAuthById(String id);

}
