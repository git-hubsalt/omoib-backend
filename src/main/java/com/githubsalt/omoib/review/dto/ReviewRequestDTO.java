package com.githubsalt.omoib.review.dto;

/**
 * @param userId                 사용자 ID
 * @param historyId              추천 히스토리 ID
 * @param temperatureSuitability 온도 적절성 (예: "추웠다", "적당했다", "더웠다")
 * @param preference             개인 선호도 (예: "별로다", "적절했다", "최고였다")
 */
public record ReviewRequestDTO(Long userId, Long historyId, String temperatureSuitability, String preference) {
}
