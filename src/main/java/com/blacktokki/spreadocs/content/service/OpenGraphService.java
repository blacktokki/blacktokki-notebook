package com.blacktokki.spreadocs.content.service;

import java.util.Optional;

import org.opengraph.OpenGraph;
import org.springframework.stereotype.Service;

import com.blacktokki.spreadocs.content.dto.PreviewRequestDto;
import com.blacktokki.spreadocs.content.dto.WebPreviewDto;

@Service
public class OpenGraphService implements PreviewService<PreviewRequestDto, WebPreviewDto> {
    static public Optional<OpenGraph> ofNullable(String url, boolean ignoreSpecErrors) {
        try {
            return Optional.of(new OpenGraph(url, ignoreSpecErrors));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public WebPreviewDto preview(PreviewRequestDto dto) {        
        Optional<OpenGraph> optional = ofNullable(dto.query(), true);
        if (optional.isPresent()){
            OpenGraph openGraph = optional.get();
            return new WebPreviewDto(
                openGraph.getContent("title"), 
                openGraph.getContent("description"),
                openGraph.getContent("url"), 
                openGraph.getContent("image"));
        }
        return new WebPreviewDto(null, null, null, null);
    }
}
