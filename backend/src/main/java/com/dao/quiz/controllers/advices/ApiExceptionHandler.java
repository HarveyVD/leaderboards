package com.dao.quiz.controllers.advices;

import com.dao.quiz.dto.ApiResponse;
import com.dao.quiz.exceptions.ErrorCode;
import com.dao.quiz.exceptions.ErrorCodeDef;
import com.dao.quiz.exceptions.UnauthorizedException;
import com.dao.quiz.exceptions.WebException;
import com.dao.quiz.utils.ProjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String DEBUGGABLE_MESSAGE = "[DEBUG-ID-{}] {}";

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorized(UnauthorizedException e, WebRequest request) {
        return handleExceptionInternal(e, new ApiResponse<>().failed(ErrorCode.UNAUTHORIZED, e.getMessage()),
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Object> handleUnauthorized(AuthenticationException e, WebRequest request) {
        return handleExceptionInternal(e, new ApiResponse<>().failed(ErrorCode.UNAUTHORIZED, e.getMessage()),
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException e, WebRequest request) {
        return handleExceptionInternal(e, ApiResponse.fail(ErrorCode.FORBIDDEN, "Access to this resource is forbidden"),
                new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(WebException.class)
    @ResponseBody
    public ResponseEntity<Object> handleWebException(WebException e, WebRequest request) {
        ErrorCodeDef errorCode = Optional.ofNullable(e.getErrorCode()).orElse(ErrorCode.INVALID_PARAMETERS);
        HttpStatus httpStatus = Optional.ofNullable(e.getHttpStatus()).orElse(HttpStatus.BAD_REQUEST);
        return handleExceptionInternal(e, new ApiResponse<>().failed(errorCode, e.getMessage(), null),
                new HttpHeaders(), httpStatus, request);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> handleBadRequest(BadRequestException e, WebRequest request) {
        log.error("Handling bad request exception", e);
        return handleExceptionInternal(e, new ApiResponse<>().failed(ErrorCode.INVALID_PARAMETERS, e.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleUnknownError(RuntimeException e, WebRequest request) {
        String debugId = ProjectUtils.randomString(10);
        log.error(DEBUGGABLE_MESSAGE, debugId, "Handling unexpected exception", e);
        return handleExceptionInternal(e, new ApiResponse<>().failedWithDebugId(ErrorCode.INTERNAL_ERROR, "unexpected error", debugId),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex, @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        return handleExceptionInternal(ex, new ApiResponse<>().failed(ErrorCode.INVALID_BODY, ex.getMessage()),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
