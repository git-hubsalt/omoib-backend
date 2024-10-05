package com.githubsalt.omoib.codyrecommendation.dto;

import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;
import com.githubsalt.omoib.history.dto.HistoryClothesDTO;

import java.util.List;

public record RecommendationAIRequestDTO(Long userId,
                                         String timestamp,
                                         List<BriefClothesDTO> clothesList,
                                         List<HistoryClothesDTO> exclude) {
}
