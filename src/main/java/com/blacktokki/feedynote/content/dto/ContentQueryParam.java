package com.blacktokki.feedynote.content.dto;

import com.blacktokki.feedynote.content.entity.ContentType;

public record ContentQueryParam(ContentType type, Long parentId, Long grandParentId, Boolean self, Boolean withDeleted){
}
