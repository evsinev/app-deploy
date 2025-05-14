package com.acme.appdeploy.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Optional;

public class GsonFile<T> {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private final Class<T> type;

    public GsonFile(Class<T> type) {
        this.type = type;
    }

    public void save(T aValue, File file) {
        try (PrintWriter out = new PrintWriter(file)) {
            GSON.toJson(aValue, out);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot write to " + file.getAbsolutePath(), e);
        }
    }

    public Optional<T> load(File file) {
        if (!file.exists()) {
            return Optional.empty();
        }

        try (FileReader in = new FileReader(file)) {
            return Optional.ofNullable(GSON.fromJson(in, type));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read from " + file.getAbsolutePath(), e);
        }
    }

}
