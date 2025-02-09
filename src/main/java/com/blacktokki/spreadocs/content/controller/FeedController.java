package com.blacktokki.spreadocs.content.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blacktokki.spreadocs.content.dto.ContentDto;
import com.blacktokki.spreadocs.content.dto.ContentQueryParam;
import com.blacktokki.spreadocs.content.dto.PullFeedDto;
import com.blacktokki.spreadocs.content.entity.ContentType;
import com.blacktokki.spreadocs.content.service.ContentService;
import com.blacktokki.spreadocs.content.service.FeedService;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {
    @Autowired
    private FeedService service;

    @Autowired
    private ContentService contentService;

    @GetMapping("/pull")
    public ResponseEntity<PullFeedDto> pull(){
        ContentQueryParam queryParam = new ContentQueryParam(ContentType.FEED, null, true, null);
        List<ContentDto> contents = contentService.getList(queryParam, Sort.unsorted());
        return ResponseEntity.ok(service.pull(contents));
    }
}
