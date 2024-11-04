package com.githubsalt.omoib.clothes.dto;

import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import java.util.List;

public record BriefClothesDTO(long id,
                              String name,
                              ClothesType type,
                              List<SeasonType> season,
                              String url) {
}
