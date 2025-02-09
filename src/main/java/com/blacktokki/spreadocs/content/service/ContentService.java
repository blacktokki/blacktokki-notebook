package com.blacktokki.spreadocs.content.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import com.blacktokki.spreadocs.content.dto.ContentDto;
import com.blacktokki.spreadocs.content.dto.ContentOrderDto;
import com.blacktokki.spreadocs.content.entity.Content;
import com.blacktokki.spreadocs.content.entity.ContentType;
import com.blacktokki.spreadocs.content.repository.ContentRepository;
import com.blacktokki.spreadocs.core.dto.BaseUserDto;
import com.blacktokki.spreadocs.core.service.restful.RestfulService;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentService extends RestfulService<ContentDto, Content, Long>{
    @Override
    public Predicate toPredicate(String key, Object value, Root<Content> root, CriteriaBuilder builder){
        if(key.equals("withDeleted")){
            return (value != null && (Boolean)value) ? null:builder.isNull(root.get("deleted"));
        }
        
        if (value == null){
            return null;
        }
        if (key.equals("self") && (Boolean)value){
            Long userId = ((BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id();
            return builder.equal(root.get("userId"), userId);
        }
        return builder.equal(root.get(key), value);
    }

    @Override
    public ContentDto toDto(Content e) {
        return new ContentDto(e.getId(), e.getUserId(), e.getParentId(), e.getType(), e.getOrder(), e.getInput(), e.getTitle(), e.getDescription(), e.getUpdated());
    }

    @Override
    public Content toEntity(ContentDto t) {
        String title = t.input();
        String description = t.description();
        boolean titleExist = title != null && !title.isBlank();
        boolean descriptionExist = description != null && !description.isBlank();
        if ((titleExist || descriptionExist) && ContentType.FEED.equals(t.type())){
            try {
                SyndFeed feed = FeedService.getFeed(t.input());
                title = feed.getTitle();
                description = feed.getDescription();
            } catch (FeedException e) {
                throw new RuntimeException(e);
            }
        }
        return Content.builder().userId(t.userId()).parentId(t.parentId()).type(t.type()).order(t.order()).input(t.input()).title(title).description(t.description()).build();
    }

    @Override
    @Transactional
    public void bulkDelete(List<Long> ids){
        Content newEntity = Content.builder().deleted(ZonedDateTime.now()).build();
        List<Content> entityList = getRepository().findAllById(ids);
        for (Content entity: entityList){
            System.out.println("@@");
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
}
