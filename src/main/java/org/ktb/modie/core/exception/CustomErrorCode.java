package org.ktb.modie.core.exception;

import java.text.MessageFormat;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CustomErrorCode implements ErrorCode {
    // 400 Bad Request
    MISSING_REQUIRED_PARAMETER(400, "E001", CustomErrorMessage.MISSING_PARAMETER),
    INVALID_PARAMETER_VALUE(400, "E002", CustomErrorMessage.INVALID_PARAMETER),
    MISSING_API_KEY(400, "E003", CustomErrorMessage.MISSING_API_KEY),
    INVALID_REQUEST_BODY(400, "E004", CustomErrorMessage.INVALID_REQUEST_BODY),
    INVALID_INPUT_VALUE(400, "E005", CustomErrorMessage.INVALID_INPUT),
    INVALID_DATE_FORMAT(400, "E006", CustomErrorMessage.INVALID_DATE_FORMAT),

    // 403 Forbidden
    INVALID_API_KEY(403, "E101", CustomErrorMessage.INVALID_API_KEY),

    // 404 Not Found
    API_NOT_FOUND(404, "E201", CustomErrorMessage.API_NOT_FOUND),
    RESOURCE_NOT_FOUND(404, "E202", CustomErrorMessage.RESOURCE_NOT_FOUND),

    // 429 Too Many Requests
    RATE_LIMIT_EXCEEDED(429, "E301", CustomErrorMessage.RATE_LIMIT_EXCEEDED),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(500, "E901", CustomErrorMessage.INTERNAL_SERVER_ERROR);

    private final int status;
    private final String code;
    private final CustomErrorMessage messageTemplate;

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return messageTemplate.getMessage();
    }

    public String getMessage(Object... args) {
        return messageTemplate.formatMessage(args);
    }

    public String formatMessage(Object... args) {
        return MessageFormat.format(getMessage(), args);
    }
}
