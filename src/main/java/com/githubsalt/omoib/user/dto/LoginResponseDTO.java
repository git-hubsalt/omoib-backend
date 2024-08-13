package com.githubsalt.omoib.user.dto;

import lombok.Builder;

@Builder
public record LoginResponseDTO(
    String token,
    Long userId
) {
}
