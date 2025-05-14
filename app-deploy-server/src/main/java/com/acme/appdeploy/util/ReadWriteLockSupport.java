package com.acme.appdeploy.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class ReadWriteLockSupport {
    private final ReadWriteLock lock      = new ReentrantReadWriteLock();
    private final Lock          readLock  = lock.readLock();
    private final Lock          writeLock = lock.writeLock();

    public void read(Runnable runnable) {
        readLock.lock();
        try {
            runnable.run();
        } finally {
            readLock.unlock();
        }
    }

    public <T> T read(Supplier<T> supplier) {
        readLock.lock();
        try {
            return supplier.get();
        } finally {
            readLock.unlock();
        }
    }

    public void write(Runnable runnable) {
        writeLock.lock();
        try {
            runnable.run();
        } finally {
            writeLock.unlock();
        }
    }

    public <T> T write(Supplier<T> supplier) {
        writeLock.lock();
        try {
            return supplier.get();
        } finally {
            writeLock.unlock();
        }
    }
}
