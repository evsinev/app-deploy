package com.acme.appdeploy.ui.service.version.impl;

import com.acme.appdeploy.dao.config.entity.TAuth;
import com.acme.appdeploy.dao.config.entity.TAuthBasic;
import com.acme.appdeploy.dao.config.model.AuthType;
import com.acme.appdeploy.dao.config.model.TVersionFetchingNginx;
import com.acme.appdeploy.ui.service.version.model.AppVersionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NginxVersionFetchingOperationExample {

    private static final Logger LOG = LoggerFactory.getLogger( NginxVersionFetchingOperationExample.class );

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("Usage: <dirUrl> <prefix> <suffix> <username> <password>");
            System.exit(1);
        }

        NginxVersionFetchingOperation operation = new NginxVersionFetchingOperation();

        List<AppVersionItem> versions = operation.fetchVersions(
                TVersionFetchingNginx.builder()
                        .dirUrl(args[0])
                        .prefix(args[1])
                        .suffix(args[2])
                        .build()
                , TAuth.builder()
                        .authType(AuthType.AUTH_BASIC)
                        .basic(TAuthBasic.builder()
                                       .username(args[3])
                                       .password(args[4])
                                       .build()
                        )
                        .build()
        );

        LOG.debug("versions count {}", versions.size());

        for (AppVersionItem version : versions) {
            LOG.debug("version {}", version);
        }
    }
}
