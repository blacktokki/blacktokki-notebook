package com.blacktokki.feedynote.core.dto;



import org.springframework.data.domain.Page;

import java.util.List;


public record PageResponseDto<T>( List<T> value, int count, int offset, int limit, long total) {
    public PageResponseDto(Page<T> page) {
        this(
            page.getContent(), 
            page.getNumberOfElements(),
            page.getPageable().getPageNumber(), 
            page.getPageable().getPageSize(), 
            page.getTotalElements());
    }
}