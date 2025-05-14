package com.acme.appdeploy.service.http.impl;

import com.acme.appdeploy.dao.config.IConfigAuthDao;
import com.acme.appdeploy.dao.config.entity.TAuth;
import com.acme.appdeploy.dao.config.entity.TAuthBasic;
import com.acme.appdeploy.service.http.IHttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Base64;

public class HttpServiceImpl implements IHttpService {

    private static final Logger LOG = LoggerFactory.getLogger( HttpServiceImpl.class );

    private final IConfigAuthDao authDao;

    public HttpServiceImpl(IConfigAuthDao authDao) {
        this.authDao = authDao;
    }

    @Override
    public HttpRequest.Builder newHttpRquestBuilder(String aAuthRef) {
        TAuth auth = authDao.findAuthById(aAuthRef);

        HttpRequest.Builder builder = HttpRequest.newBuilder();

        builder.timeout(Duration.ofSeconds(20));

        switch (auth.getAuthType()) {
            case API_KEY      -> builder.header("api-key", auth.getApiKey());
            case AUTH_BASIC   -> addBasicAuth(auth, builder);
            case BEARER_TOKEN -> builder.header("Authorization", "Bearer " + auth.getBearerToken());
            case AUTH_NONE    -> LOG.debug("no auth for {}", aAuthRef);
        }

        return builder;
    }

    private void addBasicAuth(TAuth aAuth, HttpRequest.Builder aRequest) {
        TAuthBasic basic                   = aAuth.getBasic();
        String     usernamePassword        = basic.getUsername() + ":" + basic.getPassword();
        String     encodedUsernamePassword = Base64.getEncoder().encodeToString(usernamePassword.getBytes());

        aRequest.header("Authorization", "Basic " + encodedUsernamePassword);
    }

}
