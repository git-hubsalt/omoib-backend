package com.githubsalt.omoib.virtualfitting.dto;

import com.githubsalt.omoib.clothes.dto.BriefClothesDTO;

import java.util.List;

public record FittingRequestDTO(List<BriefClothesDTO> clothes) {
}
