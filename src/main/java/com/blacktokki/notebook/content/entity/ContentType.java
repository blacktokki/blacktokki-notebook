package com.blacktokki.notebook.content.entity;

import java.util.Arrays;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum ContentType {
    NOTE, SNAPSHOT, DELTA, BOOKMARK, BOARD, UNKNOWN;
}

@Converter
class EnumConverter implements AttributeConverter<ContentType, String> {
    @Override
    public String convertToDatabaseColumn(ContentType attribute) {
        if(attribute == null) {

            return null;
        }
        return attribute.name();
    }

    @Override
    public ContentType convertToEntityAttribute(String dbData) {
        if(dbData == null){
            return null;
        }
        return Arrays.stream(ContentType.values())
            .filter( t -> t.name().equals(dbData))
            .findAny().orElse(ContentType.UNKNOWN);
    }
}