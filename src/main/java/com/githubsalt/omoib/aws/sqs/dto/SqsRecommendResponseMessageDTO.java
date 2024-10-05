package com.githubsalt.omoib.aws.sqs.dto;

import java.util.List;

public record SqsRecommendResponseMessageDTO(Long userId, String timestamp, List<Long> result) {
}
