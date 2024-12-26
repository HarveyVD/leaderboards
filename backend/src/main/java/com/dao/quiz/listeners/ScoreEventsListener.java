package com.dao.quiz.listeners;

import com.dao.quiz.events.scores.UserScoreUpdatedEvent;
import com.dao.quiz.models.domain.Leaderboard;
import com.dao.quiz.notifications.model.LeaderboardScoreUpdatedNotification;
import com.dao.quiz.repositories.UserRepository;
import com.dao.quiz.services.LeaderBoardService;
import com.dao.quiz.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.dao.quiz.constants.MessagingConstants.LEADERBOARD_PREFIX_KEY;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreEventsListener {
    @EventListener
    public void onScoreUpdatedEvent(UserScoreUpdatedEvent event) {
        // Do something needed
    }
}
