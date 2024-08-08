package com.githubsalt.omoib.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RecommendationRequestDTO {

    @NotNull
    private Long userId;

    @NotEmpty
    private List<Long> requiredClothesIdList; // 필수 지정 옷 목록

    @NotEmpty
    private List<Long> candidateClothesIdList; // 후보 옷 목록

    private List<String> filterTagList; // 옷 추천 시 필터 태그 목록

}
