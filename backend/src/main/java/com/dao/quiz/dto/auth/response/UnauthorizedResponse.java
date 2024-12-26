package com.dao.quiz.dto.auth.response;

import com.dao.quiz.dto.ApiResponse;
import com.dao.quiz.exceptions.ErrorCode;

public class UnauthorizedResponse extends ApiResponse<String> {

    public UnauthorizedResponse() {
        this.failed(ErrorCode.FORBIDDEN, "Authentication is required to access");
    }
}
