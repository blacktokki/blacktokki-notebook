package com.blacktokki.feedynote.content.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blacktokki.feedynote.content.dto.PreviewRequestDto;
import com.blacktokki.feedynote.content.dto.WebPreviewDto;
import com.blacktokki.feedynote.content.service.PreviewService;

@RestController
@RequestMapping("/api/v1/preview")
public class PreviewController {
    @Autowired
    private PreviewService<PreviewRequestDto, WebPreviewDto> service;

    @GetMapping("/autocomplete")
    public ResponseEntity<WebPreviewDto> autocomplete(PreviewRequestDto dto){
        return ResponseEntity.ok(service.preview(dto));
    }
}
