package com.githubsalt.omoib.user.dto;

import lombok.Builder;

@Builder
public record UpdateMypageResponseDTO(
    String name
) {
}
