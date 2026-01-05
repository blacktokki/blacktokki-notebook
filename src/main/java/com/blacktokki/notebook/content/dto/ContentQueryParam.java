package com.blacktokki.notebook.content.dto;

import java.util.List;

import com.blacktokki.notebook.content.entity.ContentType;

public record ContentQueryParam(List<ContentType> types, Long parentId, Boolean withHidden, Boolean withDeleted){
}
