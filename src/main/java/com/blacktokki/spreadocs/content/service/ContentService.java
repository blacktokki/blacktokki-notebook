package com.blacktokki.spreadocs.content.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.blacktokki.spreadocs.account.dto.UserDto;
import com.blacktokki.spreadocs.content.entity.Content;
import com.blacktokki.spreadocs.core.dto.BaseUserDto;
import com.blacktokki.spreadocs.core.service.restful.RestfulService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ContentService extends RestfulService<UserDto, Content, Long>{
    @Override
    public Predicate toPredicate(String key, Object value, Root<Content> root, CriteriaBuilder builder){
        if (value == null){
            return null;
        }
        if (key.equals("self") && (Boolean)value){
            Long userId = ((BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id();
            return builder.equal(root.get("userId"), userId);
        }
        return builder.equal(root.get(key), value);
    }
}