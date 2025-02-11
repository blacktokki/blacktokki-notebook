package com.blacktokki.spreadocs.content.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
import com.blacktokki.spreadocs.content.dto.PullFeedDto;
import com.blacktokki.spreadocs.content.entity.Content;
import com.blacktokki.spreadocs.content.entity.ContentType;
import com.blacktokki.spreadocs.content.repository.ContentRepository;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Service
public class FeedService {
    @Autowired
    private ContentRepository contentRepository;

    static public SyndFeed getFeed(String url) throws FeedException{
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
                        Optional<OpenGraph> opengraph = OpenGraphService.ofNullable(entry.getUri(), true);
                        String description = Optional.ofNullable(entry.getDescription()).map(v->v.getValue()).orElse(null);
                        String cover = null;
                        if (opengraph.isPresent()){
                            description = opengraph.get().getContent("description");
                            cover = opengraph.get().getContent("image");
                        }
                        return Content.builder()
                        .title(entry.getTitle())
                        .description(description)
                        .cover(cover)
                        .input(entry.getUri())
                        .order(i.incrementAndGet())
                        .userId(content.userId())
                        .parentId(content.id())
                        .type(ContentType.FEEDCONTENT)
                        .updated(ZonedDateTime.ofInstant(entry.getPublishedDate().toInstant(), ZoneId.systemDefault()))
                        .build();
                    }).filter(v->{
                        return v.getUpdated().isAfter(updated);
                    }).toList();
            } catch (FeedException e) {
                continue;
            }
            contentRepository.saveAll(feeds);
            if (feeds.size()> 0){
                feedIds.add(content.id());
            }
        }
        return new PullFeedDto(feedIds);
    };
}
