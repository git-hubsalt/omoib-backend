package com.githubsalt.omoib.review.dto;

/**
 * @param historyId              추천 히스토리 ID
 * @param text                   리뷰 내용
 */
public record ReviewResponseDTO(Long historyId, String text) {
}
