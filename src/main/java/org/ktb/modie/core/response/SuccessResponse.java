package org.ktb.modie.core.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Getter
@JsonRootName("response")
@Schema(description = "성공 Response")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessResponse<T> {

    @Schema(description = "성공 여부", defaultValue = "true")
    private final boolean success = true;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> SuccessResponse<T> of(T data) {
        SuccessResponse<T> successResponse = new SuccessResponse<>();
        successResponse.data = data;
        return successResponse;
    }

    public static SuccessResponse<Void> ofNoData() {
        return new SuccessResponse<>();
    }

    public ResponseEntity<SuccessResponse<T>> asHttp(HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(this);
    }

    public ResponseEntity<SuccessResponse<T>> asHttp(HttpStatus httpStatus, MediaType mediaType) {
        return ResponseEntity.status(httpStatus)
                .contentType(mediaType)
                .body(this);
    }
}
