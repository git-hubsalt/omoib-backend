package com.githubsalt.omoib.clothes.enums;

import com.githubsalt.omoib.global.interfaces.ValuedEnum;

public enum ClothesType implements ValuedEnum {
    upper(1),
    lower(2),
    shoes(3),
    bag(4),
    cap(5),
    outer(6),
    overall(7);

    private final int value;

    ClothesType(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
