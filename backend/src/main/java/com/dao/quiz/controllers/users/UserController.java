package com.dao.quiz.controllers.users;

import com.dao.quiz.constants.WebConstants;
import com.dao.quiz.controllers.base.BaseController;
import com.dao.quiz.dto.ApiResponse;
import com.dao.quiz.dto.users.UpdateScoreDTO;
import com.dao.quiz.models.domain.Leaderboard;
import com.dao.quiz.models.domain.User;
import com.dao.quiz.services.LeaderBoardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(WebConstants.API_V1_BASE_PREFIX + "/users")
public class UserController extends BaseController {
    @Autowired
    private LeaderBoardService leaderBoardService;

    @PostMapping("/{userId}/score")
    public ApiResponse<Boolean> updateScore(@PathVariable Long userId,
                                                    @Valid @RequestBody UpdateScoreDTO updateScoreDTO) {
        User user = getLoggedInUser();
        leaderBoardService.updateScore(user, updateScoreDTO.getQuizId(), updateScoreDTO.getScore());
        return ApiResponse.success(true);
    }

    @PostMapping("/quizzes/{quizId}")
    public ApiResponse<Leaderboard> joinQuiz(@PathVariable Long quizId) {
        User user = getLoggedInUser();
        Leaderboard leaderboard = leaderBoardService.getUserLeaderboardByQuiz(user.getId(), quizId).orElseGet(() -> leaderBoardService.addUserToQuiz(user, quizId));
        return ApiResponse.success(leaderboard);
    }
}
