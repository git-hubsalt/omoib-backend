package com.githubsalt.omoib.history.dto;

import com.githubsalt.omoib.history.HistoryType;

import java.util.List;

public record HistoryResponseDTO(Long historyId, HistoryType type, List<com.githubsalt.omoib.clothes.dto.ClothesWithPresignedDTO> clothesList,
                                 String fittingImageURL, String filterTagsString) {
}
