package com.githubsalt.omoib.history.dto;

import com.githubsalt.omoib.clothes.domain.Clothes;
import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.history.HistoryType;

import java.util.List;

public record HistoryResponseDTO(Long historyId, HistoryType type, List<Clothes> clothesList,
                                 String fittingImageURL, String filterTagsString) {
    public static HistoryResponseDTO of(History history) {
        return new HistoryResponseDTO(history.getId(), history.getType(), history.getClothesList(),
                history.getFittingImageURL(), history.getFilterTagsString());
    }
}
