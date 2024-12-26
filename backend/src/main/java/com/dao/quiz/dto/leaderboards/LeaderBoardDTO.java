package com.dao.quiz.dto.leaderboards;

import com.dao.quiz.dto.users.UserDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderBoardDTO {
    private Double score;
    private UserDTO user;
}
