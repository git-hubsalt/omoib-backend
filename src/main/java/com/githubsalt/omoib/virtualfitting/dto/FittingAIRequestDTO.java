package com.githubsalt.omoib.virtualfitting.dto;

public record FittingAIRequestDTO(String userId,
                                  String timestamp,
                                  String person_image_url,
                                  String upper_cloth_url,
                                  String lower_cloth_url,
                                  String mask_image_url,
                                  String cloth_type
) {
}
