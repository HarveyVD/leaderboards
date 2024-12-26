package com.dao.quiz.exceptions;

public enum ErrorCode implements ErrorCodeDef {
    GENERAL_ERROR("error.general"),
    UNAUTHORIZED("error.unauthorized"),
    INVALID_PARAMETERS("error.invalidParameter"),
    FORBIDDEN("error.forbidden"),
    INVALID_BODY("error.request.body.invalid"),
    QUIZ_NOT_FOUND("error.quiz.notFound"),
    USER_NOT_FOUND("error.user.notFound"),
    LEADERBOARD_NOT_FOUND("error.leaderboard.notFound"),
    INTERNAL_ERROR("error.internalError"),
    BAD_CREDENTIALS("error.credentials.invalid"),
    INVALID_TOKEN("error.token.invalid");
    private final String errorCodeDef;
    ErrorCode(String errorCodeDef) {
        this.errorCodeDef = errorCodeDef;
    }
    @Override
    public String getErrorCode() {
        return errorCodeDef;
    }
}
