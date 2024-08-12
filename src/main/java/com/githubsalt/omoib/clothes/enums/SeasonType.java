package com.githubsalt.omoib.clothes.enums;

import com.githubsalt.omoib.global.interfaces.ValuedEnum;

public enum SeasonType implements ValuedEnum {
    봄(1),
    여름(2),
    가을(3),
    겨울(4);

    private final int value;

    SeasonType(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
