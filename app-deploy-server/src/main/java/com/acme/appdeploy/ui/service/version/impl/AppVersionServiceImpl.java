package com.acme.appdeploy.ui.service.version.impl;

import com.acme.appdeploy.dao.config.IConfigAppDao;
import com.acme.appdeploy.dao.config.IConfigAuthDao;
import com.acme.appdeploy.dao.config.entity.TApp;
import com.acme.appdeploy.dao.config.model.TVersionFetching;
import com.acme.appdeploy.dao.config.model.TVersionFetchingNginx;
import com.acme.appdeploy.dao.config.model.VersionFetchingType;
import com.acme.appdeploy.ui.service.version.IAppVersionService;
import com.acme.appdeploy.ui.service.version.messages.AppViewRequest;
import com.acme.appdeploy.ui.service.version.messages.AvailableAppVersionsResponse;
import com.acme.appdeploy.ui.service.version.model.AppVersionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AppVersionServiceImpl implements IAppVersionService {

    private static final Logger LOG = LoggerFactory.getLogger( AppVersionServiceImpl.class );

    private final IConfigAppDao  appDao;
    private final IConfigAuthDao authDao;

    private final NginxVersionFetchingOperation nginxVersionFetchingOperation = new NginxVersionFetchingOperation();
    private final MavenVersionFetchingOperation mavenVersionFetchingOperation = new MavenVersionFetchingOperation();

    public AppVersionServiceImpl(IConfigAppDao appDao, IConfigAuthDao authDao) {
        this.appDao  = appDao;
        this.authDao = authDao;
    }

    @Override
    public AvailableAppVersionsResponse listAvailableAppVersions(AppViewRequest aRequest) {
        TApp app = appDao.findAppById(aRequest.getAppId()).orElseThrow(() -> new IllegalArgumentException("App not found: " + aRequest.getAppId()));
        return AvailableAppVersionsResponse.builder()
                .appId       (app.getAppId())
                .appVersions (getAppVersions(app.getVersionFetching()))
                .build();
    }

    private List<AppVersionItem> getAppVersions(TVersionFetching fetching) {
        List<AppVersionItem> versions;
        if (fetching.getType() == VersionFetchingType.MAVEN_METADATA_XML) {
            versions = mavenVersionFetchingOperation.fetchVersions(
                      fetching.getMavenMetadataXml()
                    , authDao.findAuthById(fetching.getMavenMetadataXml().getAuthRef())
            );
        } else {
            versions = nginxVersionFetchingOperation.fetchVersions(
                      fetching.getNginx()
                    , authDao.findAuthById(fetching.getNginx().getAuthRef())
            );
        }

        return versions
                .stream()
                .sorted((left, right) -> Long.compare(right.getAppVersionNumber(), left.getAppVersionNumber()))
                .limit(20)
                .toList()
        ;
    }
}
