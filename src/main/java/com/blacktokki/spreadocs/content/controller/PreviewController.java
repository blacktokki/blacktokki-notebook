package com.blacktokki.spreadocs.content.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blacktokki.spreadocs.content.dto.PreviewRequestDto;
import com.blacktokki.spreadocs.content.dto.WebPreviewDto;
import com.blacktokki.spreadocs.content.service.PreviewService;

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
