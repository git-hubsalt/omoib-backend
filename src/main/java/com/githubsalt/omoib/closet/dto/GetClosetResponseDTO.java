package com.githubsalt.omoib.closet.dto;

import java.util.List;

public record GetClosetResponseDTO(
    String name,
    String createDate,
    List<String> tagList,
    String imageUrl
) {
}
