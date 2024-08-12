package com.githubsalt.omoib.global.interfaces;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * 저장할 때: 열거형(Enum)의 value 값을 DB에 저장합니다.
 * 가져올 때: DB에 저장된 value 값을 읽어와, 해당하는 열거형(Enum) 값으로 변환합니다.
 */
@Converter
public class GenericEnumConverter<T extends Enum<T> & ValuedEnum> implements AttributeConverter<T, Integer> {
    private final Class<T> type;

    public GenericEnumConverter(Class<T> type) {
        this.type = type;
    }

    @Override
    public Integer convertToDatabaseColumn(T attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public T convertToEntityAttribute(Integer dbData) {
        T[] enums = type.getEnumConstants();

        for (T e : enums) {
            if (e.getValue().equals(dbData)) {
                return e;
            }
        }

        throw new UnsupportedOperationException();
    }
}
