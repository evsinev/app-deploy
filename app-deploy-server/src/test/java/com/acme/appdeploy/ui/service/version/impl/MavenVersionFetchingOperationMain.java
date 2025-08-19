package com.acme.appdeploy.ui.service.version.impl;


import com.acme.appdeploy.dao.config.entity.TAuth;
import com.acme.appdeploy.dao.config.entity.TAuthBasic;
import com.acme.appdeploy.dao.config.model.TVersionFetchingMavenMetadataXml;
import com.acme.appdeploy.ui.service.version.model.AppVersionItem;

import java.util.List;

public class MavenVersionFetchingOperationMain {

    public static void main(String[] args) {
        MavenVersionFetchingOperation fetcher = new MavenVersionFetchingOperation();
        List<AppVersionItem> versions = fetcher.fetchVersions(
                TVersionFetchingMavenMetadataXml.builder()
                        .url(args[0])
                        .removeSuffix("-jdk21")
                        .build()
                , TAuth.builder()
                        .basic(TAuthBasic.builder()
                                       .username(args[1])
                                       .password(args[2])
                                       .build()
                        )
                        .build()
        );

        for (AppVersionItem version : versions) {
            System.out.println(version);
        }
    }
}
