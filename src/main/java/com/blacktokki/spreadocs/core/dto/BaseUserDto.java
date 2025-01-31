package com.blacktokki.spreadocs.core.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface BaseUserDto {
    public Long id();
    public String username();
    public String name();   
}