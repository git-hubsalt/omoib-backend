package com.githubsalt.omoib.virtualfitting.dto;

import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;

import java.util.List;

public record FittingAIRequestDTO(Long userId,
                                  String timestamp,
                                  List<BriefClothesDTO> clothes) {
}
