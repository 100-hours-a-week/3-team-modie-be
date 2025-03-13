package org.ktb.modie.core.handler.validation;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.ktb.modie.core.handler.validation.strategy.*;
import org.ktb.modie.core.response.ErrorResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ValidationExceptionHandler {

    private final Map<Class<? extends Exception>, ExceptionStrategy> strategyMap;

    public ValidationExceptionHandler() {
        strategyMap = new HashMap<>();
        strategyMap.put(ConstraintViolationException.class, new ConstraintViolationStrategy());
        strategyMap.put(MethodArgumentTypeMismatchException.class, new TypeMismatchStrategy());
        strategyMap.put(DateTimeParseException.class, new DateTimeParseStrategy());
    }

    public List<ErrorResponse.ValidationError> handleException(Exception ex) {
        return strategyMap
                .getOrDefault(ex.getClass(), new DefaultExceptionStrategy())
                .handle(ex);
    }
}
