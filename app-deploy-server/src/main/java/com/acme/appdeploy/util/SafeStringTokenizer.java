package com.acme.appdeploy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SafeStringTokenizer {

    private static final Logger LOG = LoggerFactory.getLogger( SafeStringTokenizer.class );

    private final StringTokenizer st;

    public SafeStringTokenizer(String aText, char ... aToken) {
        st = new StringTokenizer(aText, new String(aToken));
    }

    public String next(String aName) {
        if(st.hasMoreTokens()) {
            return st.nextToken();
        }
        throw new IllegalStateException("No value for " + aName);
    }

    public String nextTrimmed(String aName) {
        return next(aName).trim();
    }

    public long nextLong(String aName) {
        return Long.parseLong(nextTrimmed(aName));
    }

    public int nextInt(String aName) {
        return Integer.parseInt(nextTrimmed(aName));
    }

    public String nextTrimmed(String aComment, String aDefaultValue) {
        if(st.hasMoreTokens()) {
            return st.nextToken().trim();
        } else {
            return aDefaultValue;
        }
    }

    public static List<String> splitToList(String aText, char ... aDelimiters) {
        StringTokenizer st  = new StringTokenizer(aText, new String(aDelimiters));
        List<String>     list = new ArrayList<>();
        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        return list;
    }

    public static Set<String> splitToSet(String aText, char ... aDelimiters) {
        StringTokenizer st  = new StringTokenizer(aText, new String(aDelimiters));
        Set<String>     set = new HashSet<>();
        while (st.hasMoreTokens()) {
            set.add(st.nextToken());
        }
        return set;
    }

    public static List<Long> splitToLongList(String aText, char ...aDelimiters) {
        return splitToLongList(aText, new String(aDelimiters));
    }

    public static List<Long> splitToLongList(String aText, String aDelimiters) {
        StringTokenizer st  = new StringTokenizer(aText, aDelimiters);
        List<Long>     list = new ArrayList<>();
        while (st.hasMoreTokens()) {
            try {
                String textToken = st.nextToken();
                list.add(Long.valueOf(textToken));
            } catch (NumberFormatException e) {
                LOG.error("Cannot parse {}", aText, e);
            }
        }
        return list;
    }

    public List<String> toList() {
        return null;
    }
}
