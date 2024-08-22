package com.githubsalt.omoib.codyrecommendation.dto;

import jakarta.validation.constraints.NotNull;

/**
 * @param historyId 추천 히스토리 ID
 */
public record RecommendationResponseDTO(@NotNull Long historyId) {
}
