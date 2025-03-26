package org.ktb.modie.core.exception;

import java.text.MessageFormat;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CustomErrorMessage {
    // 400 Bad Request
    MISSING_PARAMETER("필수 파라미터가 누락되었습니다"),
    INVALID_PARAMETER("잘못된 파라미터 값입니다"),
    MISSING_API_KEY("API 키가 누락되었습니다"),
    INVALID_REQUEST_BODY("잘못된 요청 본문입니다"),
    INVALID_INPUT("입력값이 유효하지 않습니다."),
    INVALID_DATE_FORMAT("날짜 형식이 올바르지 않습니다. (yyyy-MM-dd 형식으로 입력해주세요)"),
    INVALID_DATE_TOO_FAR("모임 날짜는 1년 이내여야 합니다."),
    INVALID_REQUEST("잘못된 요청입니다."),
    INVALID_PAGE("페이지 번호 또는 크기가 유효하지 않습니다."),
    INVALID_MEETING_DATE("모임 날짜는 현재 이후여야 합니다."),
    EXCESSIVE_COST("총 비용이 10,000,000원을 초과할 수 없습니다."),

    // 401 UnAuthorized
    INVALID_PERMISSION_CODE("유효하지 않은 인가 코드입니다."),

    // 403 Forbidden
    INVALID_API_KEY("유효하지 않은 API 키입니다"),
    UNAUTHORIZED_USER("인증되지 않은 사용자입니다."),
    PERMISSION_DENIED_NOT_OWNER("모임 생성자가 아닙니다."),
    PERMISSION_DENIED_NOT_MEMBER("모임 참여자만 탈퇴할 수 있습니다."),
    PERMISSION_DENIED_SETTLEMENT_NOT_OWNER("모임 생성자만 정산을 완료할 수 있습니다."),
    PERMISSION_DENIED_COMPLETED_NOT_OWNER("모임 생성자만 모임을 종료할 수 있습니다."),
    SETTLEMENT_PERMISSION_DENIED_NOT_MEMBER("모임 참여자가 아닙니다."),

    // 404 Not Found
    API_NOT_FOUND("요청한 API를 찾을 수 없습니다"),
    RESOURCE_NOT_FOUND("요청한 리소스를 찾을 수 없습니다"),
    USER_NOT_FOUND("해당 유저를 찾을 수 없습니다."),
    MEETING_NOT_FOUND("해당 모임을 찾을 수 없습니다."),

    // 409 Conflict
    MEETING_CAPACITY_FULL("모임 인원이 가득 차서 참여할 수 없습니다."),
    MEETING_ALREADY_STARTED("시작된 모임은 삭제할 수 없습니다."),
    MEETING_ALREADY_ENDED("종료된 모임은 나갈 수 없습니다."),
    OPERATION_DENIED_SETTLEMENT_REQUIRED("정산 요청 후 상태 변경이 가능합니다."),
    OPERATION_DENIED_SETTLEMENT_INCOMPLETE("정산 완료 후 종료 가능합니다."),
    ALREADY_JOINED_MEET("이미 모임에 참여한 사용자입니다"),
    OWNER_CANNOT_JOIN_MEET("방장은 모임에 참여할 수 없습니다."),
    DENIED_JOIN_ALREADY_ENDED("종료되거나 완료된 모임은 나갈 수 없습니다."),
    ALREADY_EXITED_MEET("이미 탈퇴한 모임에서는 다시 나갈 수 없습니다."),

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
