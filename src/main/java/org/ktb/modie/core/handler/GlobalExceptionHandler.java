package org.ktb.modie.core.handler;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.core.exception.ErrorCode;
import org.ktb.modie.core.handler.validation.ValidationExceptionHandler;
import org.ktb.modie.core.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String LOG_FORMAT = "[{}] {}";
    private final ValidationExceptionHandler validationExceptionHandler;

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error(LOG_FORMAT, "BusinessException", ex.getMessage(), ex);
        return createErrorResponse(ex.getErrorCode(),
            ErrorResponse.of(ex.getErrorCode(), null, ex.getMessage()));
    }

    /**
     * 유효성 검증 예외 처리
     */
    @ExceptionHandler({
        ConstraintViolationException.class,
        DateTimeParseException.class,
        MethodArgumentTypeMismatchException.class
    })
    protected ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex) {
        log.error(LOG_FORMAT, "ValidationException", ex.getMessage(), ex);

        List<ErrorResponse.ValidationError> validationErrors =
            validationExceptionHandler.handleException(ex);

        return createErrorResponse(
            CustomErrorCode.INVALID_INPUT_VALUE,
            ErrorResponse.of(CustomErrorCode.INVALID_INPUT_VALUE, validationErrors)
        );
    }

    /**
     * 필수값 누락 예외 처리
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        MissingServletRequestParameterException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        log.error(LOG_FORMAT, "MissingParameterException", ex.getMessage(), ex);

        List<ErrorResponse.ValidationError> validationErrors = List.of(
            ErrorResponse.ValidationError.of(
                ex.getParameterName(),
                CustomErrorCode.MISSING_REQUIRED_PARAMETER.formatMessage(ex.getParameterName()),
                null
            )
        );

        return createErrorResponse(
            CustomErrorCode.MISSING_REQUIRED_PARAMETER,
            ErrorResponse.of(CustomErrorCode.MISSING_REQUIRED_PARAMETER, validationErrors)
        );
    }

    /**
     * @Valid 검증 예외 처리
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {

        log.error(LOG_FORMAT, "MethodArgumentNotValidException", ex.getMessage(), ex);

        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> ErrorResponse.ValidationError.of(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue()
            ))
            .collect(Collectors.toList());

        return createErrorResponse(
            CustomErrorCode.INVALID_REQUEST_BODY,
            ErrorResponse.of(CustomErrorCode.INVALID_REQUEST_BODY, validationErrors)
        );
    }

    /**
     * 예상치 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex) {
        log.error(LOG_FORMAT, "UnexpectedException", ex.getMessage(), ex);
        return createErrorResponse(
            CustomErrorCode.INTERNAL_SERVER_ERROR,
            ErrorResponse.of(CustomErrorCode.INTERNAL_SERVER_ERROR)
        );
    }

    private <T> ResponseEntity<T> createErrorResponse(ErrorCode errorCode, T body) {
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(body);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        log.error(LOG_FORMAT, "HttpMessageNotReadableException", ex.getMessage(), ex);

        List<ErrorResponse.ValidationError> validationErrors = List.of(
            ErrorResponse.ValidationError.of(
                "meetAt",
                "날짜 형식이 잘못되었습니다. yyyy-MM-dd'T'HH:mm:ss 형식으로 입력해주세요.",
                null
            )
        );

        return createErrorResponse(
            CustomErrorCode.INVALID_DATE_FORMAT,
            ErrorResponse.of(CustomErrorCode.INVALID_DATE_FORMAT, validationErrors)
        );
    }
}
