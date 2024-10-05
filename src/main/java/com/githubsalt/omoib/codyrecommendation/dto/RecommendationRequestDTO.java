package com.githubsalt.omoib.codyrecommendation.dto;

import com.githubsalt.omoib.global.enums.ClothesStorageType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @param requiredClothesIdList  필수 지정 옷 목록
 * @param candidateClothesIdList 후보 옷 목록
 * @param filterTagList          옷 추천 시 필터 태그 목록
 */
public record RecommendationRequestDTO(@NotNull Long userId, @NotEmpty List<Long> requiredClothesIdList,
                                       @NotEmpty List<Long> candidateClothesIdList,
                                       @NotEmpty ClothesStorageType storageType, List<String> filterTagList) {

}
