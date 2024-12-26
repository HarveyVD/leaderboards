package com.dao.quiz.dto.auth.response;

import com.dao.quiz.dto.ApiResponse;
import com.dao.quiz.exceptions.ErrorCode;

public class ForbiddenResponse extends ApiResponse<String> {

    public ForbiddenResponse() {
        this.failed(ErrorCode.FORBIDDEN, "Access to this resource is denied");
    }
}
