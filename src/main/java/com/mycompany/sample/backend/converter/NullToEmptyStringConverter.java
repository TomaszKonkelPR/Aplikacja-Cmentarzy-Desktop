package com.mycompany.sample.backend.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class NullToEmptyStringConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        // Zapis do bazy — jeśli null, to zapisujemy pusty string
        return attribute == null ? "" : attribute;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // Odczyt z bazy — jeśli null, to zwracamy pusty string
        return dbData == null ? "" : dbData;
    }
}
