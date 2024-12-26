package com.dao.quiz.notifications.model;

import com.dao.quiz.notifications.NotificationSource;
import com.dao.quiz.notifications.NotificationTarget;
import com.dao.quiz.notifications.NotificationType;

import java.io.Serializable;

public interface Notification extends Serializable {
    NotificationType getNotificationType();

    NotificationSource getSourceType();

    NotificationTarget getTarget();
}
