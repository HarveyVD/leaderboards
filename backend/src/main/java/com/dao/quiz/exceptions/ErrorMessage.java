package com.dao.quiz.exceptions;

import java.text.MessageFormat;

public class ErrorMessage {
    private final ErrorCode errorCode;
    private final String messageCode;
    ErrorMessage(ErrorCode errorCode, String messageCode) {
        this.errorCode = errorCode;
        this.messageCode = messageCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public String getMessage(Object... args) {
        return MessageFormat.format(messageCode, args);
    }
}
