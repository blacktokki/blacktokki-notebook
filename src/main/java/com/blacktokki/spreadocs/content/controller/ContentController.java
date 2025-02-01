package com.blacktokki.spreadocs.content.controller;

import org.springframework.web.bind.annotation.RestController;

import com.blacktokki.spreadocs.content.dto.ContentDto;
import com.blacktokki.spreadocs.content.dto.ContentQueryParam;
import com.blacktokki.spreadocs.core.controller.RestfulController;

@RestController
public class ContentController extends RestfulController<ContentDto, ContentQueryParam, Long>{
}
