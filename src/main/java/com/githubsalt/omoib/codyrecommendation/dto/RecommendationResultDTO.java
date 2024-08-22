package com.githubsalt.omoib.codyrecommendation.dto;

import com.githubsalt.omoib.clothes.domain.Clothes;

import java.util.List;

/**
 * @param userId      사용자 ID
 * @param clothesList 추천된 옷 ID 목록
 */
public record RecommendationResultDTO(long userId, List<Clothes> clothesList) {
}
