package com.githubsalt.omoib.codyrecommendation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RecommendationResponseDTO(@NotNull Long userId, List<Long> clothesIdList) {
}
