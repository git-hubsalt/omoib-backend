package com.githubsalt.omoib.wish.dto;

import java.util.List;

public record GetWishResponseDTO(
    String name,
    String createDate,
    List<String> tagList,
    String imageUrl
) {
}
