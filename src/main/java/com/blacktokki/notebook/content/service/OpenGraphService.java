package com.blacktokki.notebook.content.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.opengraph.OpenGraph;
import org.springframework.stereotype.Service;

import com.blacktokki.notebook.content.dto.PreviewRequestDto;
import com.blacktokki.notebook.content.dto.WebPreviewDto;

@Service
public class OpenGraphService implements PreviewService<PreviewRequestDto, WebPreviewDto> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    static public Optional<OpenGraph> ofNullable(String url, boolean ignoreSpecErrors) {
        try {
            return Optional.of(new OpenGraph(url, ignoreSpecErrors));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getHtmlTitle(String urlString) {
        try {
            return Jsoup.connect(urlString)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/138.0.0.0 Safari/537.36")
                .referrer("https://www.google.com")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .timeout(5_000)
                .get()
                .title();
        } catch (IOException e) {
            return null;
        }
    }

    private WebPreviewDto getYoutubePreview(String urlString) {
        try {
            String oembedUrl = "https://www.youtube.com/oembed?url=" + urlString + "&format=json";
            
            String jsonBody = Jsoup.connect(oembedUrl)
                    .ignoreContentType(true)
                    .execute()
                    .body();

            JsonNode root = objectMapper.readTree(jsonBody);
            
            String title = root.path("title").asText();
            String author = root.path("author_name").asText();
            String thumbnailUrl = root.path("thumbnail_url").asText();
            
            return new WebPreviewDto(title, "YouTube Video by " + author, urlString, thumbnailUrl);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public WebPreviewDto preview(PreviewRequestDto dto) {
        if (dto.query() != null && (dto.query().contains("youtube.com/watch") || dto.query().contains("youtu.be/"))) {
            WebPreviewDto youtubePreview = getYoutubePreview(dto.query());
            if (youtubePreview != null && youtubePreview.title() != null) {
                return youtubePreview;
            }
        }

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
