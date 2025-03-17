package com.blacktokki.notebook.content.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blacktokki.notebook.content.dto.ContentBulkDto;
import com.blacktokki.notebook.content.dto.ContentDto;
import com.blacktokki.notebook.content.dto.ContentOrderDto;
import com.blacktokki.notebook.content.dto.ContentQueryParam;
import com.blacktokki.notebook.content.entity.Content;
import com.blacktokki.notebook.content.repository.ContentRepository;
import com.blacktokki.notebook.core.dto.BaseUserDto;
import com.blacktokki.notebook.core.service.restful.RestfulService;

@Service
public class ContentService extends RestfulService<ContentDto, Content, Long> {
    @Override
    public Predicate toPredicate(String key, Object value, Root<Content> root, CriteriaBuilder builder){
        if(key.equals("withDeleted")){
            return (value != null && (Boolean)value) ? null:builder.isNull(root.get("deleted"));
        }
        
        if (value == null){
            return null;
        }
        if (key.equals("grandParentId")){
            ContentQueryParam queryParam = new ContentQueryParam(null, (Long)value, null, true, null);
            List<Long> parentIds = getList(queryParam, Sort.unsorted()).stream().map(v->v.id()).toList();
            return root.get("parentId").in(parentIds);
        }
        if (key.equals("self")){
            if ((Boolean)value){
                Long userId = ((BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id();
                return builder.equal(root.get("userId"), userId);
            }
            return null;
        }
        return builder.equal(root.get(key), value);
    }

    @Override
    public ContentDto toDto(Content e) {
        return new ContentDto(e.getId(), e.getUserId(), e.getParentId(), e.getType(), e.getOrder(), e.getTitle(), e.getDescription(), e.getOption(), e.getUpdated());
    }

    @Override
    public Content toEntity(ContentDto t) {
        return Content.builder().userId(t.userId()).parentId(t.parentId()).type(t.type()).order(t.order()).title(t.title()).description(t.description()).option(t.option()).build();
    }

    @Override
    @Transactional
    public void bulkDelete(List<Long> ids){
        Content newEntity = Content.builder().deleted(ZonedDateTime.now()).build();
        List<Content> entityList = getRepository().findAllById(ids);
        for (Content entity: entityList){
            setEntityNotNullFields(entity, newEntity);
            System.out.println(entity);
        }
        getRepository().saveAll(entityList); 
    }

    @Transactional
    public void updateOrder(List<ContentOrderDto> list){
        list.forEach(req -> 
            ((ContentRepository)getRepository()).updateOrder(req.id(), req.order())
        );
    }

    @Transactional
    public List<ContentOrderDto> bulk(ContentBulkDto bulkDto) {
        List<Long> deleteIds = this.getList(bulkDto.deleted(), Sort.unsorted()).stream().map(v->v.id()).toList();
        this.bulkDelete(deleteIds);
        return bulkDto.created().stream().map((dto)->{
            ContentDto result = this.create(dto);
            return new ContentOrderDto(result.id(), result.order());
        }).toList();
    
    }
}
