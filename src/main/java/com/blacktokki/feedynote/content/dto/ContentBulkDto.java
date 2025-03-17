package com.blacktokki.feedynote.content.dto;

import java.util.List;

public record ContentBulkDto(List<ContentDto> created, ContentQueryParam deleted) {
}
