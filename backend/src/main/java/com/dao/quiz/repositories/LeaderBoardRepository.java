package com.dao.quiz.repositories;

import com.dao.quiz.models.domain.Leaderboard;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderBoardRepository extends JpaRepository<Leaderboard, Long> {
    @Query("SELECT l FROM Leaderboard l WHERE l.user.id = :userId AND l.quiz.id = :quizId")
    Optional<Leaderboard> findByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Leaderboard l WHERE l.user.id = :userId AND l.quiz.id = :quizId")
    Optional<Leaderboard> findLeaderboardForUpdate(@Param("userId") Long userId, @Param("quizId") Long quizId);

    @Query("SELECT l FROM Leaderboard l WHERE l.quiz.id = :quizId ORDER BY l.score DESC")
    List<Leaderboard> findScoresByQuizId(@Param("quizId") Long quizId, Pageable pageable);

    @Query("SELECT l FROM Leaderboard l WHERE l.quiz.id = :quizId and l.user.id in :userIds")
    List<Leaderboard> findByQuizIdAndUserIdIn(@Param("quizId") Long quizId, @Param("userIds") List<Long> userIds);

    List<Leaderboard> findByIdIn(List<Long> leaderBoardIds);
}
