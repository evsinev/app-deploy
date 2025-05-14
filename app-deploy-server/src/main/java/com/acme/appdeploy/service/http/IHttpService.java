package com.acme.appdeploy.service.http;

import java.net.http.HttpRequest;

public interface IHttpService {

    HttpRequest.Builder newHttpRquestBuilder(String aAuthRef);

}
