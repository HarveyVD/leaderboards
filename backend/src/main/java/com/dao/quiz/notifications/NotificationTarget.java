package com.dao.quiz.notifications;

import com.dao.quiz.models.domain.User;
import lombok.Getter;

@Getter
public class NotificationTarget {
    private final NotificationType notificationType;
    private final User user;
    private final Long quizId;

    public NotificationTarget(NotificationType notificationType, User user, Long quizId) {
        this.notificationType = notificationType;
        this.user = user;
        this.quizId = quizId;
    }
}
