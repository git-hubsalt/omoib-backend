package com.githubsalt.omoib.clothes.dto;

import java.util.List;

public record GetClothesResponseDTO(
    List<ClothesItemDTO> tops,
    List<ClothesItemDTO> bottoms,
    List<ClothesItemDTO> shoes,
    List<ClothesItemDTO> etcs
) {
    public record ClothesItemDTO(
        String name,
        String createDate,
        List<String> tagList,
        String imageUrl
    ){
    }
}
