package com.blacktokki.notebook.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.blacktokki.notebook.core.dto.BaseUserDto;
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

    public String getPatDescription() {
        for (GrantedAuthority a : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            if (a.getAuthority().startsWith("PAT_")) {
                return a.getAuthority().substring(4);
            }
        }
        return null;
    }

    public List<String> getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(v->v.getAuthority()).toList();
    }
}
