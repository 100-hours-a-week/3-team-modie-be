package org.ktb.modie.presentation.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmTokenRequest {
    @NotBlank
    private String token;

    @Size(max = 20)
    private String deviceType;
}
