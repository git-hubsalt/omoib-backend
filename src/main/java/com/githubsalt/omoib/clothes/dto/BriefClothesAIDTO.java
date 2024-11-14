package com.githubsalt.omoib.clothes.dto;

import com.githubsalt.omoib.clothes.enums.ClothesType;

public record BriefClothesAIDTO(long id,
                                String name,
                                ClothesType type,
                                String url) {
    public static BriefClothesAIDTO of(BriefClothesDTO dto) {
        return new BriefClothesAIDTO(dto.id(), dto.name(), dto.type(), dto.url());
    }
}
