package org.ktb.modie.core.handler.validation.strategy;

import org.ktb.modie.core.response.ErrorResponse;

import java.util.List;

public interface ExceptionStrategy {
    List<ErrorResponse.ValidationError> handle(Exception ex);
}
