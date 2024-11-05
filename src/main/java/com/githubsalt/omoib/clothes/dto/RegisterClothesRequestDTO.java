package com.githubsalt.omoib.clothes.dto;

import com.githubsalt.omoib.clothes.enums.SeasonType;
import java.util.List;

/**
 * 옷장에 옷 등록하기
 */
public record RegisterClothesRequestDTO(
    List<RegisterClothesDTO> clothes
) {
    public record RegisterClothesDTO(
            String name,
            List<SeasonType> seasonType,
            String clothesType
    ) {
    }
}
