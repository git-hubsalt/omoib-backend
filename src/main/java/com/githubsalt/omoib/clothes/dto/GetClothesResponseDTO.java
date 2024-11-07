package com.githubsalt.omoib.clothes.dto;

import java.util.List;

public record GetClothesResponseDTO(
        List<ClothesItemDTO> clothes
) {
    public record ClothesItemDTO(
            Long id,
            String name,
            String createDate,
            List<String> tagList,
            String imageUrl
    ){
    }
}