package com.githubsalt.omoib.clothes.dto;

import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import com.githubsalt.omoib.global.enums.ClothesStorageType;

import java.time.LocalDateTime;
import java.util.List;

public record ClothesWithPresignedDTO(Long id,
                                      String name,
                                      ClothesType clothesType,
                                      List<SeasonType> seasonType,
                                      String imagePath,
                                      ClothesStorageType clothesStorageType,
                                      LocalDateTime createAt,
                                      LocalDateTime updateAt) {
    public static ClothesWithPresignedDTO of(Long id, String name, ClothesType clothesType, List<SeasonType> seasonType, String imagePath, ClothesStorageType clothesStorageType, LocalDateTime createAt, LocalDateTime updateAt) {
        return new ClothesWithPresignedDTO(id, name, clothesType, seasonType, imagePath, clothesStorageType, createAt, updateAt);
    }
}
