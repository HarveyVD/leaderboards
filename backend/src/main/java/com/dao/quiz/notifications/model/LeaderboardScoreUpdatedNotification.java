package com.dao.quiz.notifications.model;

import com.dao.quiz.models.domain.Leaderboard;
import com.dao.quiz.models.domain.User;
import com.dao.quiz.notifications.NotificationSource;
import com.dao.quiz.notifications.NotificationTarget;
import com.dao.quiz.notifications.NotificationType;
import lombok.Getter;

import java.util.List;

import static com.dao.quiz.notifications.NotificationSource.LEADERBOARD;
import static com.dao.quiz.notifications.NotificationType.LEADERBOARD_SCORE_UPDATED;

@Getter
public class LeaderboardScoreUpdatedNotification implements Notification {
    private final User user;
    private final Long quizId;
    private final List<Leaderboard> leaderboards;
    public LeaderboardScoreUpdatedNotification(Long quizId, User user, List<Leaderboard> leaderboards) {
        this.user = user;
        this.quizId = quizId;
        this.leaderboards = leaderboards;
    }
    @Override
    public NotificationType getNotificationType() {
        return LEADERBOARD_SCORE_UPDATED;
    }

    @Override
    public NotificationSource getSourceType() {
        return LEADERBOARD;
    }

    @Override
    public NotificationTarget getTarget() {
        return new NotificationTarget(LEADERBOARD_SCORE_UPDATED, user, quizId);
    }
}
