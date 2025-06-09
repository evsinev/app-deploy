package com.acme.appdeploy.util;

import java.util.List;

public class SizeFormatter {

    private static final List<String> UNITS = List.of("B", "KiB", "MiB", "GiB", "TiB", "PiB", "EiB", "ZiB", "YiB");

    public static String formatSize(long numberOfBytes) {
        if (numberOfBytes <= 0) {
            return numberOfBytes + "B";
        }

        double exponent = Math.min(
                  Math.floor(Math.log(numberOfBytes) / Math.log(1024))
                , UNITS.size() - 1
        );

      double approx = numberOfBytes / Math.pow(1024, exponent);
      return exponent == 0
              ? numberOfBytes + " bytes"
              : (long)approx + " " + UNITS.get((int) exponent);
    }
}
