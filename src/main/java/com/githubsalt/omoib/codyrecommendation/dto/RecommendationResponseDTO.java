package com.githubsalt.omoib.codyrecommendation.dto;

import com.githubsalt.omoib.clothes.dto.GetClothesResponseDTO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecommendationResponseDTO(@NotNull Long userId, List<GetClothesResponseDTO.ClothesItemDTO> clothesList) {
}
