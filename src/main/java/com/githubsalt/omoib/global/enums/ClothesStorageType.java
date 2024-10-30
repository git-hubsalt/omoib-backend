package com.githubsalt.omoib.global.enums;

import com.githubsalt.omoib.global.interfaces.ValuedEnum;

public enum ClothesStorageType implements ValuedEnum {
    CLOSET(1),     //옷장
    WISHLIST(2)        //위시리스트
    ;

    private final int value;

    ClothesStorageType(int value) {
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
