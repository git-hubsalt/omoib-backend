package com.githubsalt.omoib.history.dto;

import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.review.Review;
import com.githubsalt.omoib.review.dto.ReviewResponseBriefDTO;

public record HistoryReviewDTO(HistoryResponseDTO history, ReviewResponseBriefDTO review) {
    public static HistoryReviewDTO build(History history, Review review) {
        return new HistoryReviewDTO(HistoryResponseDTO.of(history), new ReviewResponseBriefDTO(review != null ? review.getText() : ""));
    }
}