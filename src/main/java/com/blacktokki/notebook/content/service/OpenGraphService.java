package com.blacktokki.notebook.content.service;

import java.io.IOException;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.opengraph.OpenGraph;
import org.springframework.stereotype.Service;

import com.blacktokki.notebook.content.dto.PreviewRequestDto;
import com.blacktokki.notebook.content.dto.WebPreviewDto;

@Service
public class OpenGraphService implements PreviewService<PreviewRequestDto, WebPreviewDto> {
    static public Optional<OpenGraph> ofNullable(String url, boolean ignoreSpecErrors) {
        try {
            return Optional.of(new OpenGraph(url, ignoreSpecErrors));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static String getHtmlTitle(String urlString) {
        try {
            return Jsoup.connect(urlString).get().title();
        } catch (IOException e) {
            return null;
        }
    }


    @Override
    public WebPreviewDto preview(PreviewRequestDto dto) {        
        Optional<OpenGraph> optional = ofNullable(dto.query(), true);
        if (optional.isPresent()){
            OpenGraph openGraph = optional.get();
            return new WebPreviewDto(
                Optional.ofNullable(openGraph.getContent("title")).orElse(getHtmlTitle(dto.query())), 
                openGraph.getContent("description"),
                Optional.ofNullable(openGraph.getContent("url")).orElse(dto.query()), 
                openGraph.getContent("image"));
        }
        return new WebPreviewDto(getHtmlTitle(dto.query()), null, dto.query(), null);
    }
}
