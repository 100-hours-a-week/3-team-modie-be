package org.ktb.modie.presentation.v1.dto;

import lombok.Builder;

@Builder
public record UserDto(
    String userId,
    String userName,
    boolean isPayed
) {
}
