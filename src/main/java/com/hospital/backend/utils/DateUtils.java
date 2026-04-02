package com.hospital.backend.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;

@Slf4j
public class DateUtils {
    public static final String DATE_OR_PATTERN_REQUIRED = "Date or pattern must not be null or empty";
    private static final String DATE_STRING_OR_PATTERN_REQUIRED =
            "Date string or pattern must not be null or empty";
    // Common date formats
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String CUSTOM_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    /** Múi giờ mặc định VN */
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    /** Một số pattern hay dùng */
    public static final String ISO_DATE_TIME = "uuuu-MM-dd'T'HH:mm:ss";
    public static final String ISO_DATE = "uuuu-MM-dd";
    public static final String TIME_HH_MM_SS = "HH:mm:ss";
    /** Ví dụ: 31-08-2025 00:00:00 */
    public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-uuuu HH:mm:ss";
    /**
     * Prevent instantiation of utility class.
     */
    private DateUtils() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated!"
        );
    }
    private static DateTimeFormatter fmt(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                .withResolverStyle(ResolverStyle.SMART);
    }
    /* ------------ Format ------------- */

    /** Format LocalDateTime theo pattern (không timezone) */
    public static String format(LocalDateTime ldt, String pattern) {
        if (ldt == null) return null;
        return ldt.format(fmt(pattern));
    }

    /** Format Date theo pattern, quy chiếu theo DEFAULT_ZONE */
    public static String format(Date date, String pattern) {
        return format(date, pattern, DEFAULT_ZONE);
    }

    /** Format Date theo pattern và ZoneId chỉ định */
    public static String format(Date date, String pattern, ZoneId zone) {
        if (date == null) return null;
        Objects.requireNonNull(zone, "zone");
        return fmt(pattern).format(Instant.ofEpochMilli(date.getTime()).atZone(zone));
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null || pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException(DATE_OR_PATTERN_REQUIRED);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        Instant instant = date.toInstant();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        return zdt.format(formatter);
    }

    public static int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public static LocalDateTime parseLocalDateTime(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(s, formatter);
    }
}
