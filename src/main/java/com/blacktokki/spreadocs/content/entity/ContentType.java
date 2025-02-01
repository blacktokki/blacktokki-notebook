package com.blacktokki.spreadocs.content.entity;

import java.util.Arrays;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum ContentType {
    FEEDGROUP, FEED, FEEDCONTENT, LIBRARY, SCRAP, NOTE;
    
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
    public ContentType convertToEntityAttribute(String dbData) {	//Entity로 반환
        if(dbData == null){
            return null;
        }
        return Arrays.stream(ContentType.values())
            .filter( t -> t.name().equals(dbData))
            .findAny().orElse(null);
    }
}