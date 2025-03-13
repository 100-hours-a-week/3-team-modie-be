package org.ktb.modie.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ktb.modie.core.exception.ErrorCode;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@JsonRootName("error")
@Schema(description = "실패 Response")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    @Schema(description = "성공 여부", defaultValue = "false")
    private final boolean success = false;
    private final ErrorInfo data;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(new ErrorInfo(errorCode.getCode(), errorCode.getMessage(), null));
    }

    public static ErrorResponse of(ErrorCode errorCode, List<ValidationError> validationErrorList) {
        return new ErrorResponse(new ErrorInfo(errorCode.getCode(), errorCode.getMessage(), validationErrorList));
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(new ErrorInfo(errorCode.getCode(), message, null));
    }

    public record ValidationError(
            String field,
            String message,
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Object rejectedValue
    ) {
        public static ValidationError of(final FieldError fieldError) {
            return new ValidationError(
                    fieldError.getField(),
                    fieldError.getDefaultMessage(),
                    fieldError.getRejectedValue()
            );
        }

        public static ValidationError of(String field, String message, Object rejectedValue) {
            return new ValidationError(field, message, rejectedValue);
        }
    }

    private record ErrorInfo(String code,
                             String message,
                             @JsonInclude(JsonInclude.Include.NON_EMPTY)
                             List<ValidationError> errors) {
    }
}
