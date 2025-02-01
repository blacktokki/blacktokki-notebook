package com.blacktokki.spreadocs.content.dto;

import java.time.ZonedDateTime;

import com.blacktokki.spreadocs.content.entity.ContentType;

public record ContentDto(Long id, Long userId, Long parentId, ContentType type, String input, String title, String content, ZonedDateTime updated){
}