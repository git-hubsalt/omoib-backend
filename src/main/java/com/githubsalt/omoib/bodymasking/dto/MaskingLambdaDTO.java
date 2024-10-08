package com.githubsalt.omoib.bodymasking.dto;

import com.githubsalt.omoib.aws.dto.AWSLambdaInvocable;

public record MaskingLambdaDTO(String username, String row_image_url, String timestamp) implements AWSLambdaInvocable {
}
