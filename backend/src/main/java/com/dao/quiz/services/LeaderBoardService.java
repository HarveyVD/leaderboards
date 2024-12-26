package com.dao.quiz.services;

import com.dao.quiz.models.domain.Leaderboard;
import com.dao.quiz.models.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LeaderBoardService {
    @Transactional
    public void updateScore(User user, Long quizId, Integer score);
    public Leaderboard addUserToQuiz(User user, Long quizId);
    public Optional<Leaderboard> getUserLeaderboardByQuiz(Long userId, Long quizId);
    public List<Leaderboard> fetchLeaderboards(Long quizId, Integer count);
}
