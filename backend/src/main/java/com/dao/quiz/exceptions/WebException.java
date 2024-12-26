package com.dao.quiz.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

@Getter
public class WebException extends RuntimeException {
    private final ErrorCodeDef errorCode;
    private final HttpStatus httpStatus;
    private final ErrorMessage errorMessage;

    private final String message;

    public WebException(HttpStatus httpStatus, ErrorMessage errorMessage, Object... args) {
        this.httpStatus = httpStatus;
        this.message = errorMessage.getMessage(args);
        this.errorMessage = errorMessage;
        this.errorCode = errorMessage.getErrorCode();
    }

    public WebException(HttpStatus status, ErrorCodeDef errorCode, String message, Object... args) {
        this.httpStatus = status;
        this.message = args.length > 0 ? MessageFormat.format(message, args) : message;
        this.errorMessage = null;
        this.errorCode = errorCode;
    }

    public static WebException unauthorized(String message) {
        return new WebException(HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN, message);
    }

    public static WebException unauthorized(ErrorCodeDef errorCode, String message) {
        return new WebException(HttpStatus.UNAUTHORIZED, errorCode, message);
    }
}
