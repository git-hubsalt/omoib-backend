package com.githubsalt.omoib.review.dto;

/**
 * @param temperatureSuitability 온도 적절성 (예: "추웠다", "적당했다", "더웠다")
 * @param preference             개인 선호도 (예: "별로다", "적절했다", "최고였다")
 */
public record ReviewResponseBriefDTO(String temperatureSuitability, String preference) {
}
