package com.githubsalt.omoib.codyrecommendation.dto;

import com.githubsalt.omoib.clothes.dto.GetClothesResponseDTO;

import java.util.List;

public record RecommendationResponseDTO(List<GetClothesResponseDTO.ClothesItemDTO> clothesList) {
}
