package com.acme.appdeploy.core.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListFiles {

    public static List<File> listSortedFiles(File aDir, FileFilter aFilter) {
        File[] files = aDir.listFiles(aFilter);
        if (files == null) {
            return Collections.emptyList();
        }

        Arrays.sort(files, Comparator.comparing(File::getName));

        return Arrays.asList(files);
    }

}
