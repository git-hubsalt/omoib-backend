package com.githubsalt.omoib.review.dto;

/**
 * @param userId                 사용자 ID
 * @param historyId              추천 히스토리 ID
 * @param text                   리뷰 내용
 */
public record ReviewRequestDTO(Long userId, Long historyId, String text) {
}
