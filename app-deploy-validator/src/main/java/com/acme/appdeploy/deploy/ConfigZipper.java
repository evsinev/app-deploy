package com.acme.appdeploy.deploy;

import com.acme.appdeploy.core.util.ListFiles;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Zips the <em>contents</em> of a config directory (entries are relative to the directory itself,
 * no top level folder), reproducing what {@code (cd config && zip -r ../package.zip .)} does.
 */
public final class ConfigZipper {

    private ConfigZipper() {
    }

    public static byte[] zipContents(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + dir);
        }

        Path root = dir.toPath();
        List<File> files = new ArrayList<>();
        collectFiles(dir, files);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            for (File file : files) {
                String entryName = root.relativize(file.toPath()).toString().replace(File.separatorChar, '/');
                zip.putNextEntry(new ZipEntry(entryName));
                zip.write(Files.readAllBytes(file.toPath()));
                zip.closeEntry();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot zip " + dir, e);
        }
        return out.toByteArray();
    }

    private static void collectFiles(File dir, List<File> out) {
        for (File child : ListFiles.listSortedFiles(dir, f -> true)) {
            if (child.isDirectory()) {
                collectFiles(child, out);
            } else if (child.isFile()) {
                out.add(child);
            }
        }
    }
}
