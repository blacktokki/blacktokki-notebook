package com.blacktokki.notebook.content.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blacktokki.notebook.account.entity.User;
import com.blacktokki.notebook.content.dto.ContentDto;
import com.blacktokki.notebook.content.dto.ContentOrderDto;
import com.blacktokki.notebook.content.entity.Content;
import com.blacktokki.notebook.content.entity.ContentOption;
import com.blacktokki.notebook.content.entity.ContentType;
import com.blacktokki.notebook.content.repository.ContentRepository;
import com.blacktokki.notebook.core.service.UtilService;
import com.blacktokki.notebook.core.service.restful.RestfulService;

@Service
@RequiredArgsConstructor
public class ContentService extends RestfulService<ContentDto, Content, Long> {
    private final DiffMatchPatch dmp = new DiffMatchPatch();

    private final UtilService utilService;

    private Boolean otpChecked(Long userId) { 
        if (utilService.getPatDescription() != null) {
            return true;
        }
        Optional<Content> privacyOtpRequired = ((ContentRepository) getRepository()).findByTypeAndTitleAndUserId(ContentType.CONFIG, ContentType.CONFIG_PRIVATE_OTP_REQUIRED, userId);
        if (privacyOtpRequired.isEmpty()) {
            return true;
        }
        User user = privacyOtpRequired.get().getUser();
        return checkOtpToken(user, privacyOtpRequired.get(), false);
    }

    private Boolean checkOtpToken(User user, Content privacyOtpRequired, Boolean isOnce) {
        boolean otpRequired = privacyOtpRequired.getDescription().equals("true");
        if (isOnce) {
            if (!user.useOtp()) {
                return otpRequired;
            }
            if (otpRequired) {
                return utilService.getAuthorities().contains("OTP_ONCE");
            }
            return true;
        }
        else {
            if (!user.useOtp()) {
                return true;
            }
            if (otpRequired) {
                return utilService.getAuthorities().contains("OTP");
            }
            return true;
        }
    }

    @Override
    public Predicate toPredicate(String key, Object value, Root<Content> root, CriteriaBuilder builder){
        if (key.equals("withHidden")){
            Long userId = utilService.getUser().id();;
            Predicate predicate = builder.equal(root.get("userId"), userId);
            if (value == null || (!(Boolean) value) || !otpChecked(userId)){
                Predicate isNotHidden = builder.not(builder.or(
                    builder.like(root.get("title"), ".%"), 
                    builder.like(root.get("title"), "%/.%")));
                return builder.and(predicate, isNotHidden);
            }
            return predicate;
        }
        if(key.equals("withDeleted")){
            return (value != null && (Boolean)value) ? null:builder.isNull(root.get("deleted"));
        }
        
        if (value == null){
            return null;
        }
        if (key.equals("types")){
            List<?> list = (List<?>)value;
            return root.get("type").in(list);
        }
        return builder.equal(root.get(key), value);
    }

    @Override
    public Boolean updatable(List<Content> contents) {
        Content privacyOtpRequired = null;
        for (Content c : contents) {
            if (c.getType().equals(ContentType.CONFIG) && c.getTitle().equals(ContentType.CONFIG_PRIVATE_OTP_REQUIRED)) {
                privacyOtpRequired = c;
            }
        };
        if (privacyOtpRequired != null) {
            Long userId = utilService.getUser().id();;
            User user = privacyOtpRequired.getUser();
            if (user.getId() != userId) {
                return false;
            }
            return checkOtpToken(user, privacyOtpRequired, true);
        }
        return true;
    }

    @Override
    public ContentDto toDto(Content e) {
        return new ContentDto(e.getId(), e.getUserId(), e.getParentId(), e.getType(), e.getOrder(), e.getTitle(), e.getDescription(), e.getOption(), e.getUpdated());
    }

    @Override
    public Content toEntity(ContentDto t) {
        return Content.builder().userId(t.userId()).parentId(t.parentId()).type(t.type()).order(t.order()).title(t.title()).description(t.description()).option(t.option()).build();
    }

    private void addPatDescription(ContentDto newDomain) {
        String description = utilService.getPatDescription();
        if (description != null) {
            newDomain.option().put(ContentOption.PAT_DESCRIPTION, description);
        }
    }

    private void createDelta(ContentDto newDomain) {
        // 가장 최근에 생성한 SNAPSHOT을 조건부로 DELTA로 변경한다.
        List<Content> lastContents = ((ContentRepository) getRepository()).findByTypeAndParentIdOrderByIdDesc(
            Pageable.ofSize(2), ContentType.SNAPSHOT, newDomain.parentId());
        if (lastContents.size() == 2) {
            Content newer = lastContents.get(0);
            Content older = lastContents.get(1);
            Long diffCount = ((ContentRepository) getRepository()).countByTypeAndParentIdAndIdGreaterThan(
                ContentType.DELTA, older.getParentId(), older.getId());
            LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(older.getDescription(), newer.getDescription());
            dmp.diffCleanupSemantic(diffs);
            String delta = dmp.diffToDelta(diffs);
            if (diffCount < 19 && delta.length() < newer.getDescription().length()) {
                ContentOption.Map option = new ContentOption.Map(newer.getOption());
                option.put(ContentOption.SNAPSHOT_ID, older.getId());
                Content deltaContent = Content.builder()
                    .id(newer.getId())
                    .userId(newer.getUserId())
                    .parentId(newer.getParentId())
                    .type(ContentType.DELTA)
                    .order(newer.getOrder())
                    .title(newer.getTitle())
                    .description(delta)
                    .option(option)
                    .updated(newer.getUpdated())
                    .build();
                getRepository().save(deltaContent);
            }
        }
    }

    @Override
    @Transactional
    public ContentDto create(ContentDto newDomain){
        if (newDomain.type().equals(ContentType.SNAPSHOT)) {
            addPatDescription(newDomain);
            createDelta(newDomain);
        }
        return super.create(newDomain);
    }

    @Override
    @Transactional
    public void bulkDelete(List<Long> ids){
        Content newEntity = Content.builder().deleted(ZonedDateTime.now()).build();
        List<Content> entityList = getRepository().findAllById(ids);
        for (Content entity: entityList){
            setEntityNotNullFields(entity, newEntity);
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
