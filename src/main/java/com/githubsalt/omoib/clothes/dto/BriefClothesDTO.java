package com.githubsalt.omoib.clothes.dto;

import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;

public record BriefClothesDTO(long id,
                              String name,
                              ClothesType type,
                              SeasonType season,
                              String url) {
}
