package com.dao.quiz.controllers.leaderboards;

import com.dao.quiz.constants.WebConstants;
import com.dao.quiz.dto.ApiResponse;
import com.dao.quiz.dto.leaderboards.RankedLeaderBoardDTO;
import com.dao.quiz.mappers.DTOBuilder;
import com.dao.quiz.services.LeaderBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(WebConstants.API_V1_BASE_PREFIX + "/leaderboards")
public class LeaderboardController {
    @Autowired
    LeaderBoardService leaderBoardService;

    @GetMapping("/top/{count}")
    public ApiResponse<List<RankedLeaderBoardDTO>> getTopKLeaderboards(@PathVariable Integer count,
                                                                       @RequestParam(name = "quiz_id") Long quizId) {
        return ApiResponse.success(DTOBuilder.toDTO(leaderBoardService.fetchLeaderboards(quizId, count), RankedLeaderBoardDTO.class));
    }
}
