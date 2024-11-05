package com.githubsalt.omoib.user.dto;

import lombok.Builder;

@Builder
public record GetMypageResponseDTO(
    String name,
    String email,
    String rowImagePath,
    String profileImagePath
) {
}
