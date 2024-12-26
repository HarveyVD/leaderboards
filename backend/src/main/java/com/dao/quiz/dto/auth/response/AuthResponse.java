package com.dao.quiz.dto.auth.response;

import com.dao.quiz.models.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private User user;

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
