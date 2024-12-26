package com.dao.quiz.services.leaderboards;

import com.dao.quiz.events.publisher.MessageBroker;
import com.dao.quiz.events.scores.UserScoreUpdatedEvent;
import com.dao.quiz.exceptions.LeaderBoardNotFoundException;
import com.dao.quiz.exceptions.QuizNotFoundException;
import com.dao.quiz.models.domain.Leaderboard;
import com.dao.quiz.models.domain.Quiz;
import com.dao.quiz.models.domain.User;
import com.dao.quiz.notifications.model.LeaderboardScoreUpdatedNotification;
import com.dao.quiz.repositories.LeaderBoardRepository;
import com.dao.quiz.repositories.QuizRepository;
import com.dao.quiz.services.LeaderBoardService;
import com.dao.quiz.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.dao.quiz.constants.MessagingConstants.LEADERBOARD_PREFIX_KEY;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderBoardService {
    private final LeaderBoardRepository leaderBoardRepository;
    private final QuizRepository quizRepository;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final MessageBroker messageBroker;
    private final NotificationService notificationService;
    @Override
    public Optional<Leaderboard> getUserLeaderboardByQuiz(Long userId, Long quizId) {
        return leaderBoardRepository.findByUserIdAndQuizId(userId, quizId);
    }

    @Override
    @Transactional
    public void updateScore(User user, Long quizId, Integer score) {
        // 1. Update DB
        Leaderboard leaderboard = leaderBoardRepository.findLeaderboardForUpdate(user.getId(), quizId)
                .orElseThrow(() -> new LeaderBoardNotFoundException(user.getId(), quizId));
        leaderboard.setScore(score);
        leaderBoardRepository.save(leaderboard);

        // 2. Update Cache
        String key = getLeaderboardPrefixKey(quizId);
        stringRedisTemplate.opsForZSet().add(key, String.valueOf(leaderboard.getId()), score);

        // 3. Fetch top K leaderboards from cache and send via WebSocket
        List<Leaderboard> topLeaderboards = fetchLeaderboards(quizId, 10);
        notificationService.processNotification(
            new LeaderboardScoreUpdatedNotification(quizId, user, topLeaderboards)
        );

        // 4. Optional: Publish event for other purposes
        messageBroker.sendEventToQueue(
            new UserScoreUpdatedEvent(user.getId(), quizId, leaderboard.getId(), score)
        );
    }

    @Override
    public Leaderboard addUserToQuiz(User user, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(QuizNotFoundException::new);
        // 1. Create New in DB
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.setUser(user);
        leaderboard.setQuiz(quiz);
        leaderboard.setScore(0);
        leaderBoardRepository.save(leaderboard);
        // 2. Add new record to cache
        String key = getLeaderboardPrefixKey(quizId);
        stringRedisTemplate.opsForZSet().add(key, String.valueOf(leaderboard.getId()), 0);
        // 3. Fetch top K leaderboards from cache and send via WebSocket
        List<Leaderboard> topLeaderboards = fetchLeaderboards(quizId, 10);
        notificationService.processNotification(
                new LeaderboardScoreUpdatedNotification(quizId, user, topLeaderboards)
        );
        return leaderboard;
    }

    public List<Leaderboard> fetchLeaderboards(Long quizId, Integer count) {
        String key = getLeaderboardPrefixKey(quizId);
        Set<String> topLeaderBoardIds = stringRedisTemplate.opsForZSet().reverseRange(key, 0, count);
        if (topLeaderBoardIds != null && topLeaderBoardIds.isEmpty()) {
            // Fetch from database
            Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Order.desc("score")));
            List<Leaderboard> topLeaderBoards = leaderBoardRepository.findScoresByQuizId(quizId, pageable);
            
            // Batch update cache using TypedTuple
            Set<ZSetOperations.TypedTuple<String>> scoreTuples = topLeaderBoards.stream()
                    .map(lb -> new DefaultTypedTuple<>(
                        String.valueOf(lb.getId()),
                        lb.getScore().doubleValue()
                    ))
                    .collect(Collectors.toSet());
            stringRedisTemplate.opsForZSet().add(key, scoreTuples);
            
            return topLeaderBoards;
        }

        // Convert to List maintaining Redis ZSet order
        List<Long> leaderBoardIds = topLeaderBoardIds.stream()
                .map(Long::valueOf)
                .toList();
        
        // Get leaderboards from DB and maintain Redis order
        Map<Long, Leaderboard> leaderboardMap = leaderBoardRepository.findByIdIn(leaderBoardIds).stream()
                .collect(Collectors.toMap(Leaderboard::getId, lb -> lb));
                
        // Return in same order as Redis ZSet
        return leaderBoardIds.stream()
                .map(leaderboardMap::get)
                .filter(Objects::nonNull)  // Handle any missing records
                .toList();
    }

    private String getLeaderboardPrefixKey(Long quizId) {
        return String.format("%s_%s", LEADERBOARD_PREFIX_KEY, quizId);
    }

    private List<Long> fetchTopLeaderboardsFromCache(String leaderboardPrefixKey, Integer count) {
        return stringRedisTemplate.opsForZSet().reverseRange(leaderboardPrefixKey, 0, count).stream().map(Long::valueOf).toList();
    }
}
