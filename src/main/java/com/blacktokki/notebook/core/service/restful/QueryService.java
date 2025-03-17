package com.blacktokki.notebook.core.service.restful;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.blacktokki.notebook.core.dto.PageResponseDto;

public interface QueryService<T, ID> {
    public PageResponseDto<T> getPage(Object param, Pageable pageable);

    public List<T> getList(Object param, Sort sort);

    public Optional<T> getOptional(ID id);
}
