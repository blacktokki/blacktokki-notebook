package com.blacktokki.notebook.content.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blacktokki.notebook.content.dto.ContentDto;
import com.blacktokki.notebook.content.dto.ContentOrderDto;
import com.blacktokki.notebook.content.dto.ContentQueryParam;
import com.blacktokki.notebook.content.service.ContentService;
import com.blacktokki.notebook.core.controller.RestfulController;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController extends RestfulController<ContentDto, ContentQueryParam, Long>{
    @Autowired
    private ContentService service;

    @PutMapping("/order")
    public ResponseEntity<Void> bulkUpdateOrder(@RequestBody List<ContentOrderDto> updated){
        service.updateOrder(updated);
        return ResponseEntity.ok().build();
    }
}
