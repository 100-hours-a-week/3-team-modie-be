package org.ktb.modie.core.handler.validation.strategy;

import java.time.format.DateTimeParseException;
import java.util.List;

import org.ktb.modie.core.exception.CustomErrorCode;
import org.ktb.modie.core.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateTimeParseStrategy implements ExceptionStrategy {
    @Override
    public List<ErrorResponse.ValidationError> handle(Exception ex) {
        DateTimeParseException dtpe = (DateTimeParseException)ex;
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        ex.printStackTrace();
        return List.of(
            ErrorResponse.ValidationError.of(
                "date",
                CustomErrorCode.INVALID_DATE_FORMAT.getMessage(),
                dtpe.getParsedString()
            )
        );
    }
}
