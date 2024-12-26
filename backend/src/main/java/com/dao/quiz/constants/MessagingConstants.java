package com.dao.quiz.constants;

public class MessagingConstants {
    private static final String MESSAGES_SUFFIX = "/messages";
    public static final String NOTIFICATIONS_SUFFIX = "/notifications";

    public static final String LEADERBOARD_PREFIX_KEY = "leaderboard";
    public static final String QUEUE_DEFAULT_NAME = "/queue";

    public static final String QUEUE_LEADERBOARD = QUEUE_DEFAULT_NAME + "/leaderboards";
    public static final String DESTINATION_APP_PREFIX = "/app/";
    public static final String DESTINATION_USER_PREFIX = "/user/";
    public static final String QUEUE_LEADERBOARD_NOTIFICATION = QUEUE_LEADERBOARD + NOTIFICATIONS_SUFFIX;
}
