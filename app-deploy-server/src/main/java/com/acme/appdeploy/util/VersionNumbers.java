package com.acme.appdeploy.util;

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
}
