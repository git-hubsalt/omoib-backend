package com.githubsalt.omoib.clothes.dto;

public record BriefClothesAIDTO(long id,
                                String name,
                                String type,
                                String url) {
    public static BriefClothesAIDTO of(BriefClothesDTO dto) {
        return new BriefClothesAIDTO(dto.id(), dto.name(), dto.clothesType(), dto.url());
    }
}
