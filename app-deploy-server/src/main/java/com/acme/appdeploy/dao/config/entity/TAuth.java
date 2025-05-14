package com.acme.appdeploy.dao.config.entity;

import com.acme.appdeploy.dao.config.model.AuthType;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class TAuth {

    String     authId;
    AuthType   authType;
    TAuthBasic basic;
    String     bearerToken;
    String     apiKey;

}
