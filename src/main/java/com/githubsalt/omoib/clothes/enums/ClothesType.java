package com.githubsalt.omoib.clothes.enums;

import com.githubsalt.omoib.global.interfaces.ValuedEnum;
import lombok.Getter;

public enum ClothesType implements ValuedEnum {
    upper(1, "상의"),
    lower(2, "하의"),
    shoes(3, "신발"),
    bag(4, "가방"),
    cap(5, "모자"),
    outer(6, "아우터"),
    overall(7, "한벌옷"),
    other(8, "기타");

    private final int value;
    @Getter
    private final String description;

    ClothesType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ClothesType fromDescription(String description) {
        for (ClothesType type : ClothesType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("올바르지 않은 옷 카테고리입니다. " + description);
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

}
