package com.blacktokki.spreadocs.content.dto;

import com.blacktokki.spreadocs.content.entity.ContentType;

public record ContentQueryParam(ContentType type, Long parentId, Boolean self, Boolean withDeleted){
}
