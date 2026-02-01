package com.blacktokki.notebook.content.entity;

import java.util.EnumMap;
 

import jakarta.persistence.AttributeConverter;
 

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
 

public enum ContentOption {
    SNAPSHOT_ID, BOARD_TYPE, BOARD_NOTE_IDS, BOARD_HEADER_LEVEL, PAT_DESCRIPTION;
    static public class Map extends EnumMap<ContentOption, Object> {
        public Map() {
            super(ContentOption.class);
        }
        public Map(Map map) {
            super(map);
        }
    }

    static public class Converter implements AttributeConverter<Map, String> {
        static private final ObjectMapper objectMapper = new ObjectMapper();
 
       @Override
        public String convertToDatabaseColumn(Map attribute) {
            try {
                return  objectMapper.writeValueAsString(attribute);
            } catch (Exception e) {
                return null;
            }
        }
 
        @Override
        public Map convertToEntityAttribute(String dbData) {
            if (dbData==null) {
                return new Map();
            }
            TypeReference<Map> result=new TypeReference<>() {
            };
            try {
                return objectMapper.readValue(dbData, result);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
