package com.dao.quiz.dto;

import com.dao.quiz.exceptions.ErrorCode;
import com.dao.quiz.exceptions.ErrorCodeDef;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private ResponseStatus status;
    private String errorCode;
    private String reason;
    private T result;
    private String debugId;

    public ApiResponse() {
        this(ResponseStatus.SUCCESS);
    }

    public ApiResponse(T data) {
        this();
        this.result = data;
    }

    public ApiResponse(ResponseStatus status) {
        this.status = status;
    }

    public static <T> ApiResponse<T> success(T response) {
        return new ApiResponse<T>().successResponse(response);
    }

    public static ApiResponse<String> forbiddenResponse(String message) {
        return new ApiResponse<String>().failed(ErrorCode.FORBIDDEN, StringUtils.isNotEmpty(message) ? message : "Access to this resource is forbidden");
    }

    public static ApiResponse<String> fail(ErrorCodeDef errorCode, String message) {
        return new ApiResponse<String>().failed(errorCode, message);
    }

    public ApiResponse<T> failedWithDebugId(ErrorCode errorCode, String reason, String debugId) {
        ApiResponse<T> failed = failed(errorCode, reason, null);
        failed.debugId = debugId;
        return failed;
    }

    public ApiResponse<T> failed(ErrorCodeDef errorCode, String reason) {
        return this.failed(errorCode, reason, null);
    }

    public ApiResponse<T> failed(ErrorCodeDef errorCode, String reason, T response) {
        this.status = ResponseStatus.FAILED;
        this.reason = reason;
        if (errorCode != null) {
            this.errorCode = errorCode.getErrorCode();
        } else {
            this.errorCode = null;
        }
        this.result = response;
        return this;
    }

    public ApiResponse<T> failed(String reason) {
        return this.failed(ErrorCode.GENERAL_ERROR, reason);
    }

    public ApiResponse<T> successResponse(T data) {
        this.status = ResponseStatus.SUCCESS;
        this.result = data;
        return this;
    }

    @JsonProperty("status")
    public ResponseStatus getStatus() {
        return status;
    }

    @JsonProperty("errorCode")
    public String getErrorCode() {
        return errorCode;
    }

    @JsonProperty("message")
    public String getReason() {
        return reason;
    }

    @JsonProperty("result")
    public T getResult() {
        return result;
    }

    protected void setStatus(ResponseStatus status) {
        this.status = status;
    }

    @JsonProperty("debugId")
    public String getDebugId() {
        return debugId;
    }
}
