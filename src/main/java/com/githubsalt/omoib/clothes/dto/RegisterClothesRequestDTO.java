package com.githubsalt.omoib.clothes.dto;

import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;

/**
 * 옷장에 옷 등록하기
 * @param name
 * @param seasonType
 * @param clothesType
 */
public record RegisterClothesRequestDTO(
    String name,
    SeasonType seasonType,
    ClothesType clothesType
) {
}
