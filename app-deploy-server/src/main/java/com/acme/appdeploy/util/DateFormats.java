package com.acme.appdeploy.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class DateFormats {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS z");

    public static String formatEpoch(long aEpoch) {
        return formatEpoch(aEpoch, ZoneId.systemDefault());
    }

    public static String formatEpoch(long aEpoch, ZoneId zone) {
        if (aEpoch <= 0) {
            return "-";
        }

        Instant       instant       = Instant.ofEpochMilli(aEpoch);
        ZonedDateTime zonedDateTime = instant.atZone(zone);

        return zonedDateTime.format(DATE_TIME_FORMAT);
    }

    public static String formatAgo(long aWhen) {
        return formatAgo(System.currentTimeMillis(), aWhen);
    }

    public static String formatAgo(long aCurrentDate, long aWhen) {
        if (aCurrentDate == aWhen) {
            return "moments ago";
        }

        if (aWhen <= 0) {
            return "";
        }

        long msInDay = 86_400_000;
        long millis = aCurrentDate -aWhen;

        if (millis <= 0) {
            return "";
        }

        Function<Long, LocalDate> longToLocalDate = (epochMs) -> LocalDate.ofEpochDay(epochMs / msInDay);

        if (millis > msInDay) {
            Period period = Period.between(longToLocalDate.apply(aWhen), longToLocalDate.apply(aCurrentDate));
            return new PeriodBuilder()
                    .add(period.getYears() , "year")
                    .add(period.getMonths(), "month")
                    .add(period.getDays()  , "day")
                    .toString();
        }

        if (millis < 1_000) {
            return millis + "ms";
        }

        Duration duration = Duration.ofMillis(millis);

        return new PeriodBuilder()
                .add(duration.toHoursPart() , "hour")
                .add(duration.toMinutesPart(), "min")
                .add(duration.toSecondsPart(), "sec")
                .toString();
    }

    private static class PeriodBuilder {
        private final StringBuilder buf = new StringBuilder();

        private PeriodBuilder add(int value, String unit) {
            if (value == 0) {
                return this;
            }

            if (!buf.isEmpty()) {
                buf.append(" ");
            }

            buf.append(value).append(" ").append(unit);

            if (value > 1) {
                buf.append("s");
            }

            return this;
        }

        public String toString() {
            return buf.toString();
        }
    }
}
