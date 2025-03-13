package org.ktb.modie.core.handler.validation.strategy;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.ktb.modie.core.response.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ConstraintViolationStrategy implements ExceptionStrategy {
    @Override
    public List<ErrorResponse.ValidationError> handle(Exception ex) {
        ConstraintViolationException cve = (ConstraintViolationException) ex;
        return cve.getConstraintViolations()
                .stream()
                .map(violation -> ErrorResponse.ValidationError.of(
                        extractFieldName(violation.getPropertyPath().toString()),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .collect(Collectors.toList());
    }

    private String extractFieldName(String propertyPath) {
        String[] parts = propertyPath.split("\\.");
        return parts[parts.length - 1];
    }
}
