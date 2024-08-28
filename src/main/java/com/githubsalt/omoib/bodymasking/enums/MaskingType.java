package com.githubsalt.omoib.bodymasking.enums;

import com.githubsalt.omoib.global.interfaces.ValuedEnum;

public enum MaskingType implements ValuedEnum {
    SHIRT(1),
    OUTER(2),
    PANTS(3),
    SKIRT(4),
    DRESS(5);

    private final int value;

    MaskingType(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}