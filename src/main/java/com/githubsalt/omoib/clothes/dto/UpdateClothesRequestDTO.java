package com.githubsalt.omoib.clothes.dto;

import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import java.util.List;

/**
 * 옷장에 옷 수정하기
 * @param name
 * @param seasonType
 * @param clothesType
 */
public record UpdateClothesRequestDTO(
    String name,
    List<SeasonType> seasonType,
    ClothesType clothesType
) {
}
