package com.acme.appdeploy.ui.service.version.impl;

import com.acme.appdeploy.dao.config.entity.TAuth;
import com.acme.appdeploy.dao.config.entity.TAuthBasic;
import com.acme.appdeploy.dao.config.model.TNginxVersionFetching;
import com.acme.appdeploy.ui.service.version.model.AppVersionItem;
import com.acme.appdeploy.util.SafeStringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

import static com.acme.appdeploy.util.SizeFormatter.formatSize;
import static com.acme.appdeploy.util.VersionNumbers.versionNumber;

public class NginxVersionFetchingOperation {

    private static final Logger LOG = LoggerFactory.getLogger( NginxVersionFetchingOperation.class );

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public List<AppVersionItem> fetchVersions(TNginxVersionFetching aNginx, TAuth aAuth) {

        HttpRequest httpRequest = addAuth(
                aAuth
                , HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(aNginx.getDirUrl()))
                    .timeout(Duration.ofSeconds(20))
                )
                .build();

        LOG.debug("Fetching versions from {}", aNginx.getDirUrl());

        HttpResponse<String> httpResponse;
        try {
            httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot fetch " + aNginx.getDirUrl(), e);
        }

        if (httpResponse.statusCode() != 200) {
            throw new IllegalArgumentException("Cannot fetch " + aNginx.getDirUrl() + " - " + httpResponse.statusCode());
        }

        return parseHtml(httpResponse.body(), aNginx.getPrefix(), aNginx.getSuffix());
    }

    private List<AppVersionItem> parseHtml(String body, String aPrefix, String aSuffix) {
        List<AppVersionItem> versions = new ArrayList<>();
        StringTokenizer linesTokenizer = new StringTokenizer(body, "\n\r");
        while (linesTokenizer.hasMoreTokens()) {
            String line = linesTokenizer.nextToken();

            if (!line.startsWith("<a href=\"")) {
                continue;
            }

            if (!line.contains(aSuffix + "\">")) {
                continue;
            }

            versions.add(parseLine(line, aPrefix, aSuffix));
        }
        return versions;
    }

    static AppVersionItem parseLine(String line, String aPrefix, String aSuffix) {
        String begin = "<a href=\"" + aPrefix;
        String end   = aSuffix + "\">";

        int beginIndex = line.indexOf(begin);
        if (beginIndex < 0) {
            throw new IllegalArgumentException("Cannot parse line: " + line + ": no prefix " + aPrefix);
        }

        int endIndex = line.indexOf(end, beginIndex + begin.length());
        if( endIndex < 0 ) {
            throw new IllegalArgumentException("Cannot parse line: " + line + ": no suffix " + aSuffix);
        }

        String version = line.substring(beginIndex + begin.length(), endIndex);

        SafeStringTokenizer st   = new SafeStringTokenizer(line.substring(endIndex), ' ', '\n');

        String skip = st.next("skip");
        String date = st.next("date");
        String time = st.next("time");
        long   size = st.nextLong("bytes");

        return AppVersionItem.builder()
                .appVersion       ( version                )
                .appVersionNumber ( versionNumber(version) )
                .timestamp        ( date + " " + time      )
                .size             ( size                   )
                .sizeFormatted    ( formatSize(size)       )
                .build();
    }

    private HttpRequest.Builder addAuth(TAuth aAuth, HttpRequest.Builder aRequest) {
        TAuthBasic basic                   = aAuth.getBasic();
        String     usernamePassword        = basic.getUsername() + ":" + basic.getPassword();
        String     encodedUsernamePassword = Base64.getEncoder().encodeToString(usernamePassword.getBytes());

        aRequest.header("Authorization", "Basic " + encodedUsernamePassword);

        return aRequest;
    }
}
