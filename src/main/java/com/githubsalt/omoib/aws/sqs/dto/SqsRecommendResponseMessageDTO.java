package com.githubsalt.omoib.aws.sqs.dto;

import java.util.List;

public record SqsRecommendResponseMessageDTO(String userId, String timestamp, List<Long> result) {
}
