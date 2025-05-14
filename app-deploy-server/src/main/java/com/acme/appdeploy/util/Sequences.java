package com.acme.appdeploy.util;

import java.util.concurrent.atomic.AtomicLong;

public class Sequences {

    private static final AtomicLong LOG_COUNTER = new AtomicLong(1_000_000L);

    public static String nextDeployLogId() {
        return System.currentTimeMillis() + "-" + LOG_COUNTER.incrementAndGet();
    }

}
