package com.blacktokki.notebook.content.dto;

import com.blacktokki.notebook.content.entity.ContentType;

public record ContentQueryParam(ContentType type, Long parentId, Long grandParentId, Boolean self, Boolean withDeleted){
}
