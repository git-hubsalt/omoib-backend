package com.githubsalt.omoib.aws.sqs.dto;

public record SqsRecommendResponseMessageDTO(String userId, String initial_timestamp, SqsRecommendPredictionDTO prediction) {
}
