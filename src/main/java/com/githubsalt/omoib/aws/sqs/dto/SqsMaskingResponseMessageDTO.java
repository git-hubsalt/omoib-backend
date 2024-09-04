package com.githubsalt.omoib.aws.sqs.dto;

public record SqsMaskingResponseMessageDTO(
        String username,
        String initial_timestamp,
        String payload
) {
}
