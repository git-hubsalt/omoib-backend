package com.githubsalt.omoib.codyrecommendation.dto;

import com.githubsalt.omoib.aws.dto.AWSLambdaInvocable;
import com.githubsalt.omoib.clothes.dto.BriefClothesAIDTO;
import com.githubsalt.omoib.history.dto.HistoryClothesDTO;

import java.util.List;

public record RecommendationAIRequestDTO(String userId,
                                         String timestamp,
                                         List<BriefClothesAIDTO> requiredClothes,
                                         List<BriefClothesAIDTO> clothesList,
                                         List<HistoryClothesDTO> exclude,
                                         String style) implements AWSLambdaInvocable {
}
