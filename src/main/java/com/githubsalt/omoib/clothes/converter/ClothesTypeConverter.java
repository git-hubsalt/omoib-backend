package com.githubsalt.omoib.clothes.converter;

import com.githubsalt.omoib.clothes.enums.ClothesType;
import com.githubsalt.omoib.global.interfaces.GenericEnumConverter;

public class ClothesTypeConverter extends GenericEnumConverter<ClothesType> {
    public ClothesTypeConverter() {
        super(ClothesType.class);
    }
}