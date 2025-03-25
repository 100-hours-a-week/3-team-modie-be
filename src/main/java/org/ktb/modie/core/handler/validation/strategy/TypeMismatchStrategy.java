package org.ktb.modie.core.handler.validation.strategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.core.response.ErrorResponse;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeMismatchStrategy implements ExceptionStrategy {
    @Override
    public List<ErrorResponse.ValidationError> handle(Exception ex) {
        MethodArgumentTypeMismatchException matme = (MethodArgumentTypeMismatchException)ex;
        if (matme.getRequiredType() != null && (
            LocalDate.class.isAssignableFrom(matme.getRequiredType())
                || LocalDateTime.class.isAssignableFrom(matme.getRequiredType())
                || LocalTime.class.isAssignableFrom(matme.getRequiredType())
        )) {

            // 날짜/시간 포맷을 명확하게 메시지에 담기
            String message = String.format("잘못된 날짜 형식입니다. 형식은 yyyy-MM-dd'T'HH:mm:ss 형태여야 합니다. 입력값: [%s]",
                matme.getValue());

            return List.of(
                ErrorResponse.ValidationError.of(
                    matme.getName(),
                    message,
                    matme.getValue()
                )
            );
        }

        return List.of(
            ErrorResponse.ValidationError.of(
                matme.getName(),
                CustomErrorCode.INVALID_PARAMETER_VALUE.getMessage(),
                matme.getValue()
            )
        );
    }
}
