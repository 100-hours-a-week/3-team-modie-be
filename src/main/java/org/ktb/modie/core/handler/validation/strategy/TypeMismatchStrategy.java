package org.ktb.modie.core.handler.validation.strategy;

import lombok.extern.slf4j.Slf4j;
import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.core.response.ErrorResponse;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public class TypeMismatchStrategy implements ExceptionStrategy {
    @Override
    public List<ErrorResponse.ValidationError> handle(Exception ex) {
        MethodArgumentTypeMismatchException matme = (MethodArgumentTypeMismatchException) ex;
        String field = matme.getName();
        CustomErrorCode errorCode = matme.getRequiredType() != null
                && LocalDate.class.isAssignableFrom(matme.getRequiredType())
                ? CustomErrorCode.INVALID_DATE_FORMAT
                : CustomErrorCode.INVALID_PARAMETER_VALUE;

        return List.of(
                ErrorResponse.ValidationError.of(
                        field,
                        errorCode.getMessage(),
                        matme.getValue()
                )
        );
    }
}
