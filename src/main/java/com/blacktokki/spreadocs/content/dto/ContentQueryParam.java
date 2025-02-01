package com.blacktokki.spreadocs.content.dto;

import com.blacktokki.spreadocs.content.entity.ContentType;

public record ContentQueryParam(ContentType type, Boolean self){
}
