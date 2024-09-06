package com.githubsalt.omoib.clothes.enums;

import com.githubsalt.omoib.global.interfaces.ValuedEnum;

public enum ClothesType implements ValuedEnum {
    상의(1),
    하의(2),
    신발(3),
    기타(4);

    private final int value;

    ClothesType(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
