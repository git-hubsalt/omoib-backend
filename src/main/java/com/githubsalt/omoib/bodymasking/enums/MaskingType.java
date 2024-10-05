package com.githubsalt.omoib.bodymasking.enums;

import com.githubsalt.omoib.global.interfaces.ValuedEnum;

public enum MaskingType implements ValuedEnum {
    UPPER(1),
    LOWER(3),
    OUTER(2),
    INNER(4),
    OVERALL(5);

    private final int value;

    MaskingType(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}