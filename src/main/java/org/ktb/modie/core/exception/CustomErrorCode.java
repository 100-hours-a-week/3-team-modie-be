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
    INVALID_DATE_TOO_FAR(400, "E007", CustomErrorMessage.INVALID_DATE_TOO_FAR),
    INVALID_REQUEST(400, "K001", CustomErrorMessage.INVALID_REQUEST),
    INVALID_INPUT_IN_USER(400, "U003", CustomErrorMessage.INVALID_INPUT),
    INVALID_INPUT_IN_MEET(400, "M002", CustomErrorMessage.INVALID_INPUT),
    INVALID_INPUT_MISSING_PARAMETER(400, "M004", CustomErrorMessage.INVALID_INPUT),
    INVALID_INPUT_PAGE(400, "M006", CustomErrorMessage.INVALID_PAGE),
    INVALID_MEETING_DATE(400, "M020", CustomErrorMessage.INVALID_MEETING_DATE),

    // 401 UnAuthorized
    INVALID_PERMISSION_CODE(401, "K002", CustomErrorMessage.INVALID_PERMISSION_CODE),
    INVALID_TOKEN(401, "A001", CustomErrorMessage.INVALID_TOKEN),

    // 403 Forbidden
    INVALID_API_KEY(403, "E101", CustomErrorMessage.INVALID_API_KEY),
    UNAUTHORIZED_USER_INVALID_AUTHENTICATED(403, "U001", CustomErrorMessage.UNAUTHORIZED_USER),
    UNAUTHORIZED_USER_INVALID_TOKEN(403, "U004", CustomErrorMessage.UNAUTHORIZED_USER),
    UNAUTHORIZED_USER_NOT_LOGIN(403, "M003", CustomErrorMessage.UNAUTHORIZED_USER),
    UNAUTHORIZED_USER_NOT_OWNER(403, "M005", CustomErrorMessage.UNAUTHORIZED_USER),
    PERMISSION_DENIED_NOT_OWNER(403, "M008", CustomErrorMessage.PERMISSION_DENIED_NOT_OWNER),
    PERMISSION_DENIED_NOT_MEMBER(403, "M010", CustomErrorMessage.PERMISSION_DENIED_NOT_MEMBER),
    SETTLEMENT_PERMISSION_DENIED_NOT_MEMBER(403, "M017", CustomErrorMessage.SETTLEMENT_PERMISSION_DENIED_NOT_MEMBER),
    PERMISSION_DENIED_SETTLEMENT_NOT_OWNER(403, "M012", CustomErrorMessage.PERMISSION_DENIED_SETTLEMENT_NOT_OWNER),
    PERMISSION_DENIED_COMPLETED_NOT_OWNER(403, "M014", CustomErrorMessage.PERMISSION_DENIED_COMPLETED_NOT_OWNER),
    EXCESSIVE_COST(400, "M021", CustomErrorMessage.EXCESSIVE_COST),

    // 404 Not Found
    API_NOT_FOUND(404, "E201", CustomErrorMessage.API_NOT_FOUND),
    RESOURCE_NOT_FOUND(404, "E202", CustomErrorMessage.RESOURCE_NOT_FOUND),
    USER_NOT_FOUND(404, "U002", CustomErrorMessage.USER_NOT_FOUND),
    MEETING_NOT_FOUND(404, "M001", CustomErrorMessage.MEETING_NOT_FOUND),
    FCM_TOKEN_NOT_FOUND(404, "U005", CustomErrorMessage.FCM_TOKEN_NOT_FOUND),

    // 409 Conflict
    MEETING_CAPACITY_FULL(409, "M007", CustomErrorMessage.MEETING_CAPACITY_FULL),
    MEETING_ALREADY_STARTED(409, "M009", CustomErrorMessage.MEETING_ALREADY_STARTED),
    MEETING_ALREADY_ENDED(409, "M011", CustomErrorMessage.MEETING_ALREADY_ENDED),
    OPERATION_DENIED_SETTLEMENT_REQUIRED(409, "M013", CustomErrorMessage.OPERATION_DENIED_SETTLEMENT_REQUIRED),
    OPERATION_DENIED_SETTLEMENT_INCOMPLETE(409, "M015", CustomErrorMessage.OPERATION_DENIED_SETTLEMENT_INCOMPLETE),
    ALREADY_JOINED_MEET(409, "M016", CustomErrorMessage.ALREADY_JOINED_MEET),
    OWNER_CANNOT_JOIN_MEET(409, "M018", CustomErrorMessage.OWNER_CANNOT_JOIN_MEET),
    DENIED_JOIN_ALREADY_ENDED(409, "M019", CustomErrorMessage.DENIED_JOIN_ALREADY_ENDED),
    ALREADY_EXITED_MEET(409, "M014", CustomErrorMessage.ALREADY_EXITED_MEET),

    // 429 Too Many Requests
    RATE_LIMIT_EXCEEDED(429, "E301", CustomErrorMessage.RATE_LIMIT_EXCEEDED),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(500, "E901", CustomErrorMessage.INTERNAL_SERVER_ERROR),
    FCM_TOKEN_SET_FAILED(500, "E501", CustomErrorMessage.FCM_TOKEN_SET_FAILED),
    FCM_SEND_FAILED(500, "E401", CustomErrorMessage.FCM_SEND_FAILED),
    FCM_CONNECTION_FAILED(500, "E601", CustomErrorMessage.FCM_CONNECTION_FAILED);

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
