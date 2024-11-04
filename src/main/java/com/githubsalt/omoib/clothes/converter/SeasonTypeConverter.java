package com.githubsalt.omoib.clothes.converter;

import com.githubsalt.omoib.clothes.enums.SeasonType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;

@Converter
public class SeasonTypeConverter implements AttributeConverter<List<SeasonType>, String> {
    private static final String SPLIT_CHAR = ", ";

    @Override
    public String convertToDatabaseColumn(List<SeasonType> stringList) {
        return String.join(SPLIT_CHAR, stringList.stream().map(Enum::name).toList());
    }

    @Override
    public List<SeasonType> convertToEntityAttribute(String string) {
        return Arrays.stream(string.split(SPLIT_CHAR)).map(SeasonType::valueOf).toList();
    }
}