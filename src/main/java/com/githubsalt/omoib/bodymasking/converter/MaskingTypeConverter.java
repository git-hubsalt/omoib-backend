package com.githubsalt.omoib.bodymasking.converter;

import com.githubsalt.omoib.bodymasking.enums.MaskingType;
import com.githubsalt.omoib.global.interfaces.GenericEnumConverter;

public class MaskingTypeConverter extends GenericEnumConverter<MaskingType> {
    public MaskingTypeConverter() {
        super(MaskingType.class);
    }
}
