package com.githubsalt.omoib.history.dto;

import com.githubsalt.omoib.history.History;
import com.githubsalt.omoib.review.Review;

public record HistoryReviewDTO(History history, Review review) {
}
