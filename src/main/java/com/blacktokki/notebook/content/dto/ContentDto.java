package com.blacktokki.notebook.content.dto;

import java.time.ZonedDateTime;

import com.blacktokki.notebook.content.entity.ContentOption;
import com.blacktokki.notebook.content.entity.ContentType;

public record ContentDto(Long id, Long userId, Long parentId, ContentType type, Integer order, String title, String description, ContentOption.Map option, ZonedDateTime updated){
}