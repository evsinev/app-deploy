package com.acme.appdeploy.util;

import java.util.List;

import static com.acme.appdeploy.util.SafeStringTokenizer.splitToList;

public class VersionNumbers {

    public static long versionNumber(String version) {
        SafeStringTokenizer st = new SafeStringTokenizer(version, '.', '-');
        long major = st.nextLong("major");
        long minor = st.nextLong("minor");
        long patch = st.nextLong("patch");

        return    major * 1_000_000L
                + minor *     1_000L
                + patch;
    }

    public static long versionNumberDynamic(String version) {
        List<String> tokens = splitToList(version, '.', '-');

        long versionNumber = 0L;
        long multiplier    = 1L;
        for (String token : tokens.reversed()) {
            versionNumber += multiplier * parseLongWithFilter(token);
            multiplier *= 10_000L;
        }

        return versionNumber;
    }

    private static long parseLongWithFilter(String token) {
        StringBuilder sb = new StringBuilder();
        for (char c : token.toCharArray()) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        String number = sb.toString();
        if (number.isEmpty()) {
            return 0;
        }
        return Long.parseLong(number);
    }
}
