package com.dao.quiz.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ProjectUtils {
    public static final String DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private static final String CHARS = "abcdefgjklmnprstuvwxyz0123456789";
    private static final Random RANDOM = new SecureRandom(DATETIME_FORMAT.format(LocalDateTime.now()).getBytes());
    public static String randomString(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return builder.toString();
    }
}
