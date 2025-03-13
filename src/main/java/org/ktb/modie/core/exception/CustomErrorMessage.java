package org.ktb.modie.core.exception;

import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;

@RequiredArgsConstructor
public enum CustomErrorMessage {
    // 400 Bad Request
    MISSING_PARAMETER("필수 파라미터가 누락되었습니다"),
    INVALID_PARAMETER("잘못된 파라미터 값입니다"),
    MISSING_API_KEY("API 키가 누락되었습니다"),
    INVALID_REQUEST_BODY("잘못된 요청 본문입니다"),
    INVALID_INPUT("잘못된 입력값입니다"),
    INVALID_DATE_FORMAT("날짜 형식이 올바르지 않습니다. (yyyy-MM-dd 형식으로 입력해주세요)"),

    // 403 Forbidden
    INVALID_API_KEY("유효하지 않은 API 키입니다"),

    // 404 Not Found
    API_NOT_FOUND("요청한 API를 찾을 수 없습니다"),
    RESOURCE_NOT_FOUND("요청한 리소스를 찾을 수 없습니다"),
    COMPANY_NOT_FOUND("기업 코드 {0}를 찾을 수 없습니다"),

    // 429 Too Many Requests
    RATE_LIMIT_EXCEEDED("요청 제한을 초과했습니다. {0}건/{1}초 제한"),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다");

    private final String messagePattern;

    public String getMessage() {
        return messagePattern;
    }

    public String formatMessage(Object... args) {
        return MessageFormat.format(messagePattern, args);
    }
}
