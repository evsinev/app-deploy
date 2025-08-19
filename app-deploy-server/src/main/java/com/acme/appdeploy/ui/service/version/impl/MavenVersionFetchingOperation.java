package com.acme.appdeploy.ui.service.version.impl;

import com.acme.appdeploy.dao.config.entity.TAuth;
import com.acme.appdeploy.dao.config.model.TVersionFetchingMavenMetadataXml;
import com.acme.appdeploy.ui.service.version.model.AppVersionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.acme.appdeploy.util.HttpAuths.fetchHttpWithAuth;
import static com.acme.appdeploy.util.SizeFormatter.formatSize;
import static com.acme.appdeploy.util.VersionNumbers.versionNumber;
import static com.acme.appdeploy.util.VersionNumbers.versionNumberDynamic;
import static com.payneteasy.jetty.util.Strings.hasText;
import static com.payneteasy.jetty.util.Strings.isEmpty;
import static javax.xml.stream.XMLStreamConstants.*;

public class MavenVersionFetchingOperation {

    private static final Logger LOG = LoggerFactory.getLogger( MavenVersionFetchingOperation.class );

    private final XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public List<AppVersionItem> fetchVersions(TVersionFetchingMavenMetadataXml aMaven, TAuth aAuth) {
        HttpResponse<String> httpResponse = fetchHttpWithAuth(aAuth, aMaven.getUrl(), httpClient);

        return extractVersions(httpResponse.body())
                .stream()
                .filter(it -> filterVersion(it, aMaven.getPrefix()))
                .map(it -> toItem(it, aMaven.getRemovePrefix(), aMaven.getRemoveSuffix()))
                .toList();
    }

    private boolean filterVersion(String version, String prefix) {
        if (isEmpty(version)) {
            return false;
        }

        if (hasText(prefix)) {
            return version.startsWith(prefix);
        }

        return true;
    }

    private AppVersionItem toItem(String aOriginal, String removePrefix, String removeSuffix) {
        String version = aOriginal;

        if (hasText(removePrefix)) {
            version = version.replace(removePrefix, "");
        }

        if (hasText(removeSuffix)) {
            version = version.replace(removeSuffix, "");
        }

        return AppVersionItem.builder()
                .appVersion       ( version )
                .appVersionNumber ( versionNumberDynamic(version) )
                .timestamp        ( "-" )
                .size             ( -1  )
                .sizeFormatted    ( formatSize(-1) )
                .build();
    }

    private List<String> extractVersions(String xml) {
        try {
            XMLStreamReader reader  = xmlFactory.createXMLStreamReader(new StringReader(xml));

            List<String> versions = new ArrayList<>();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == START_ELEMENT && "version".equals(reader.getLocalName())) {
                    String version = reader.getElementText().trim();
                    versions.add(version);
                }
            }
            return versions;

        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse XML for versions", e);
        }
    }


}
