package com.githubsalt.omoib.history.dto;

import com.githubsalt.omoib.history.History;

// 피그마의 테마 텍스트와 태그 필드에 관한 구분 모호함. 일단 테마 텍스트만 포함
public record HistoryBriefListDTO(Long historyId, String thumbnailImage, String tagString) {
    public static HistoryBriefListDTO of(History history) {
        return new HistoryBriefListDTO(history.getId(), history.getFittingImageURL(), history.getFilterTagsString());
    }
}
