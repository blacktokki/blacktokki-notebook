package com.blacktokki.spreadocs.core.service.restful;

import java.util.List;
import java.util.Optional;

import com.blacktokki.spreadocs.core.dto.PageResponseDto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface QueryService<T, ID> {
    public PageResponseDto<T> getPage(Object param, Pageable pageable);

    public List<T> getList(Object param, Sort sort);

    public Optional<T> getOptional(ID id);
}
