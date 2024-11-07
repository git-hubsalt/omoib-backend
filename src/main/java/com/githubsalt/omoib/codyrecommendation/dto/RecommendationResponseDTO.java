package com.githubsalt.omoib.codyrecommendation.dto;

import com.githubsalt.omoib.clothes.dto.ClothesResponseDTO;

import java.util.List;

public record RecommendationResponseDTO(List<ClothesResponseDTO.ClothesItemDTO> clothesList) {
}
