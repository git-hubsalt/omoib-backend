package com.githubsalt.omoib.aws.sqs.dto;

public record SqsRecommendPredictionDTO(Long upperId, Long lowerId, Double cpscore) {
}
