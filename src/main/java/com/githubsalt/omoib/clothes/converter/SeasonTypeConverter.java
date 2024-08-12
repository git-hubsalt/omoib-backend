package com.githubsalt.omoib.clothes.converter;

import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.clothes.enums.SeasonType;
import com.githubsalt.omoib.global.interfaces.GenericEnumConverter;

public class SeasonTypeConverter extends GenericEnumConverter<SeasonType> {
    public SeasonTypeConverter() {
        super(SeasonType.class);
    }
}