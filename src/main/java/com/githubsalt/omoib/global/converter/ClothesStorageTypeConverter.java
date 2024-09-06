package com.githubsalt.omoib.global.converter;

import com.githubsalt.omoib.global.enums.ClothesStorageType;
import com.githubsalt.omoib.global.interfaces.GenericEnumConverter;

public class ClothesStorageTypeConverter extends GenericEnumConverter<ClothesStorageType> {
    public ClothesStorageTypeConverter() {
        super(ClothesStorageType.class);
    }
}