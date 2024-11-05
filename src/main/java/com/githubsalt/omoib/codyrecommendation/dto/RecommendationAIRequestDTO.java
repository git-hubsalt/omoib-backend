package com.githubsalt.omoib.codyrecommendation.dto;

import com.githubsalt.omoib.aws.dto.AWSLambdaInvocable;
import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;
import com.githubsalt.omoib.history.dto.HistoryClothesDTO;

import java.util.List;

public record RecommendationAIRequestDTO(Long userId,
                                         String timestamp,
                                         List<BriefClothesDTO> requiredClothes,
                                         List<BriefClothesDTO> clothesList,
                                         List<HistoryClothesDTO> exclude) implements AWSLambdaInvocable {
}
