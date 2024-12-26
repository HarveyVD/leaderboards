package com.dao.quiz.controllers.quizzes;

import com.dao.quiz.constants.WebConstants;
import com.dao.quiz.dto.ApiResponse;
import com.dao.quiz.exceptions.QuizNotFoundException;
import com.dao.quiz.models.domain.Quiz;
import com.dao.quiz.services.QuizzesManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(WebConstants.API_V1_BASE_PREFIX + "/quizzes")
public class QuizController {
    @Autowired
    public QuizzesManagementService quizzesManagementService;

    @GetMapping
    public ApiResponse<List<Quiz>> getQuizzes() {
        return ApiResponse.success(quizzesManagementService.getAllQuizzes());
    }

    @GetMapping("{quizId}")
    public ApiResponse<Quiz> getQuiz(@PathVariable Long quizId) {
        return ApiResponse.success(quizzesManagementService.getQuiz(quizId).orElseThrow(QuizNotFoundException::new));
    }
}
