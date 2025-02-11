package com.blacktokki.spreadocs.content.dto;

import java.time.ZonedDateTime;

import com.blacktokki.spreadocs.content.entity.ContentType;

public record ContentDto(Long id, Long userId, Long parentId, ContentType type, Integer order, String input, String title, String description, String cover, ZonedDateTime updated){
}