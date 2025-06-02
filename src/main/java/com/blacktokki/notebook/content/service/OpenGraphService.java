package com.blacktokki.notebook.content.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        StringBuilder content = new StringBuilder();

        try {
            // URL 객체 생성
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            // 응답 스트림 읽기
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8")
            );

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
                // 제목이 있는 줄을 찾으면 빨리 끝내기
                if (line.toLowerCase().contains("</title>")) {
                    break;
                }
            }
            reader.close();

            // 정규식을 이용해 <title> 추출
            Pattern pattern = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content.toString());
            if (matcher.find()) {
                return matcher.group(1).trim();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
