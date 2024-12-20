package com.githubsalt.omoib.codyrecommendation.dto;

import java.util.List;

/**
 * @param requiredClothes  필수 지정 옷 목록
 * @param filterTagList          옷 추천 시 필터 태그 목록
 */
public record RecommendationRequestDTO(List<Long> requiredClothes, List<String> filterTagList) {

}
