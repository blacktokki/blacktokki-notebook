package com.blacktokki.spreadocs.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.blacktokki.spreadocs.core.dto.BaseUserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UtilService {
    static final ObjectMapper mapper = new ObjectMapper();

    public LocalDateTime now(){
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    public LocalDate today(){
        return now().toLocalDate();
    }

    public BaseUserDto getUser(){
        return (BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String writeValueAsString(Object value){
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
