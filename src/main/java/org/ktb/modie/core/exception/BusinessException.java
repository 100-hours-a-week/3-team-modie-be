package org.ktb.modie.core.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    // 에러 코드만 반환
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 에러 코드 + Throwable 반환
    public BusinessException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode.getMessage(), throwable);
        this.errorCode = errorCode;
    }

    // 에러 코드 + 메시지 반환
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    // 에러 코드 + 메시지 포맷팅을 위한 args 반환
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode instanceof CustomErrorCode
                ? ((CustomErrorCode) errorCode).formatMessage(args)
                : errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
