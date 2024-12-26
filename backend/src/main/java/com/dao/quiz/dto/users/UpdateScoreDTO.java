package com.dao.quiz.dto.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateScoreDTO {
    @NotNull(message = "score is required")
    @Max(100)
    @Min(1)
    private Integer score;

    @NotNull(message = "quiz is required")
    @JsonProperty("quiz_id")
    private Long quizId;
}
