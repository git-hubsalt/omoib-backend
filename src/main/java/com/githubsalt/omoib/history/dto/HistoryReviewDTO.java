package com.githubsalt.omoib.history.dto;

import com.githubsalt.omoib.review.Review;
import com.githubsalt.omoib.review.dto.ReviewResponseBriefDTO;

public record HistoryReviewDTO(HistoryResponseDTO history, ReviewResponseBriefDTO review) {

    public static HistoryReviewDTO build(HistoryResponseDTO historyResponseDTO, Review review) {
        return new HistoryReviewDTO(historyResponseDTO, new ReviewResponseBriefDTO(review != null ? review.getText() : ""));
    }
}