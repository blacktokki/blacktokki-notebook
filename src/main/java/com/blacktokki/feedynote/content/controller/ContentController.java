package com.blacktokki.feedynote.content.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blacktokki.feedynote.content.dto.ContentBulkDto;
import com.blacktokki.feedynote.content.dto.ContentDto;
import com.blacktokki.feedynote.content.dto.ContentOrderDto;
import com.blacktokki.feedynote.content.dto.ContentQueryParam;
import com.blacktokki.feedynote.content.service.ContentService;
import com.blacktokki.feedynote.core.controller.RestfulController;

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

    @PostMapping("/bulk")
    public ResponseEntity<List<ContentOrderDto>> bulk(@RequestBody ContentBulkDto bulk){
        return ResponseEntity.ok(service.bulk(bulk));
    }

}
