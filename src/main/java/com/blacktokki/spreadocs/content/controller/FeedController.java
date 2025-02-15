package com.blacktokki.spreadocs.content.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blacktokki.spreadocs.content.dto.PreviewRequestDto;
import com.blacktokki.spreadocs.content.dto.FeedPreviewDto;
import com.blacktokki.spreadocs.content.dto.PullFeedDto;
import com.blacktokki.spreadocs.content.service.FeedService;
import com.blacktokki.spreadocs.content.service.PreviewService;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {
    @Autowired
    private FeedService service;

    @Autowired
    private PreviewService<PreviewRequestDto, FeedPreviewDto> previewService;

    @GetMapping("/pull")
    public ResponseEntity<PullFeedDto> pull(){
        return ResponseEntity.ok(service.pull());
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<FeedPreviewDto> autocomplete(PreviewRequestDto dto){
        return ResponseEntity.ok(previewService.preview(dto));
    }
}
