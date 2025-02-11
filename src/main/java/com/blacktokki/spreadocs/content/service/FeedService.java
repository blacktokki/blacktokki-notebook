package com.blacktokki.spreadocs.content.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Comparator;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.opengraph.OpenGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blacktokki.spreadocs.content.dto.ContentDto;
import com.blacktokki.spreadocs.content.dto.FeedPreviewDto;
import com.blacktokki.spreadocs.content.dto.PreviewRequestDto;
import com.blacktokki.spreadocs.content.dto.PullFeedDto;
import com.blacktokki.spreadocs.content.entity.Content;
import com.blacktokki.spreadocs.content.entity.ContentType;
import com.blacktokki.spreadocs.content.repository.ContentRepository;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Service
public class FeedService implements PreviewService<PreviewRequestDto, FeedPreviewDto> {
    @Autowired
    private ContentRepository contentRepository;

    private SyndFeed getFeed(String url) throws FeedException{
        try (CloseableHttpClient client = HttpClients.createMinimal()) {
            HttpUriRequest request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request);
                InputStream stream = response.getEntity().getContent()) {
                SyndFeedInput input = new SyndFeedInput();
                    return input.build(new XmlReader(stream));
            }
        } catch (IllegalArgumentException | IOException e) {
            throw new FeedException(url);
        }
    }

    @Override
    public FeedPreviewDto preview(PreviewRequestDto dto) {
        try {
            SyndFeed feed = getFeed(dto.query());
            return new FeedPreviewDto(feed.getTitle(), feed.getDescription());
        } catch (FeedException e) {
            throw new RuntimeException(e);
        }
    };

    @Transactional
    public PullFeedDto pull(List<ContentDto> contents) {
        List<Long> feedIds = new ArrayList<>();
        for (ContentDto content: contents){
            Optional<Content> lastContent = contentRepository.lastContent(content.userId(), ContentType.FEEDCONTENT);
            AtomicInteger i = new AtomicInteger(lastContent.map(Content::getOrder).orElse(0));
            ZonedDateTime updated = lastContent.map(Content::getUpdated).orElse(Instant.ofEpochMilli(Long.MIN_VALUE).atZone(ZoneId.systemDefault()));
            List<Content> feeds;
            try {
                feeds = getFeed(content.input()).getEntries().stream().sorted(Comparator.comparing(e->e.getPublishedDate())).map(entry->{
                    ZonedDateTime entryUpdated = ZonedDateTime.ofInstant(entry.getPublishedDate().toInstant(), ZoneId.systemDefault());
                    if (!entryUpdated.isAfter(updated)){
                        return null;
                    }
                    String description = Optional.ofNullable(entry.getDescription()).map(v->v.getValue()).orElse(null);
                    Optional<OpenGraph> opengraph = OpenGraphService.ofNullable(entry.getUri(), true);
                    if (opengraph.isPresent()){
                        description = opengraph.get().getContent("description");
                        // cover = opengraph.get().getContent("image");
                    }
                    return Content.builder()
                        .title(entry.getTitle())
                        .description(description)
                        .input(entry.getUri())
                        .order(i.incrementAndGet())
                        .userId(content.userId())
                        .parentId(content.id())
                        .type(ContentType.FEEDCONTENT)
                        .updated(ZonedDateTime.ofInstant(entry.getPublishedDate().toInstant(), ZoneId.systemDefault()))
                        .build();
                    }).filter(Objects::nonNull).toList();
            } catch (FeedException e) {
                continue;
            }
            contentRepository.saveAll(feeds);
            if (feeds.size()> 0){
                feedIds.add(content.id());
            }
        }
        return new PullFeedDto(feedIds);
    }
}
