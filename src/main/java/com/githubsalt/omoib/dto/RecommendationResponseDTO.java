package com.githubsalt.omoib.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecommendationResponseDTO {

    @NotNull
    private Long historyId; // 추천 히스토리 ID

}
