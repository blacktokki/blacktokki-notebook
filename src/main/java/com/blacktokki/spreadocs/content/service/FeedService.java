package com.blacktokki.spreadocs.content.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.opengraph.OpenGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blacktokki.spreadocs.content.dto.ContentDto;
import com.blacktokki.spreadocs.content.dto.ContentQueryParam;
import com.blacktokki.spreadocs.content.dto.FeedPreviewDto;
import com.blacktokki.spreadocs.content.dto.PreviewRequestDto;
import com.blacktokki.spreadocs.content.dto.PullFeedDto;
import com.blacktokki.spreadocs.content.entity.Content;
import com.blacktokki.spreadocs.content.entity.ContentType;
import com.blacktokki.spreadocs.content.repository.ContentRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class FeedService implements PreviewService<PreviewRequestDto, FeedPreviewDto> {
    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentService contentService;


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
    public PullFeedDto pull() {
        ContentQueryParam queryParam = new ContentQueryParam(ContentType.FEED, null, true, null);
        List<ContentDto> contents = contentService.getList(queryParam, Sort.unsorted());
        return syncFeed(contents);
    }

    @Scheduled(fixedDelay = 600000)
    public void push(){
        log.info("Feed push start");
        ContentQueryParam queryParam = new ContentQueryParam(ContentType.FEED, null, false, null);
        List<ContentDto> contents = contentService.getList(queryParam, Sort.unsorted());
        PullFeedDto dto = syncFeed(contents);
        log.info("Feed push end: " + StringUtils.join(dto.updatedFeedIds(), ','));
    }

    private PullFeedDto syncFeed(List<ContentDto> feeds){
        Map<Long, List<ContentDto>> timelineMap = feeds.stream().collect(Collectors.groupingBy(v->v.parentId()));
        List<Long> timelineIds = new ArrayList<>();
        for (Map.Entry<Long, List<ContentDto>> timeline: timelineMap.entrySet()){
            List<Long> currentFeedIds = timeline.getValue().stream().map(v->v.id()).toList();
            Optional<Content> lastContent = contentRepository.lastContent(currentFeedIds);
            AtomicInteger i = new AtomicInteger(lastContent.map(Content::getOrder).orElse(0));
            ZonedDateTime updated = lastContent.map(Content::getUpdated).orElse(Instant.ofEpochMilli(Long.MIN_VALUE).atZone(ZoneId.systemDefault()));
            List<Map.Entry<ContentDto, SyndEntry>> feedMap = new ArrayList<>();
            for (ContentDto content: timeline.getValue()){   
                try {
                    feedMap.addAll(getFeed(content.input()).getEntries().stream().map(v->Map.entry(content, v)).toList());
                }
                catch (FeedException e) {
                    continue;
                }
            }
            List<Content> feedContents = feedMap.stream().sorted(Comparator.comparing(e->e.getValue().getPublishedDate())).map(entrySet->{
                ContentDto content = entrySet.getKey();
                SyndEntry entry = entrySet.getValue();
                ZonedDateTime entryUpdated = ZonedDateTime.ofInstant(entry.getPublishedDate().toInstant(), ZoneId.systemDefault());
                if (!entryUpdated.isAfter(updated)){
                    return null;
                }
                String description = Optional.ofNullable(entry.getDescription()).map(v->v.getValue()).orElse(null);
                String imageUrl = null;
                Optional<OpenGraph> opengraph = OpenGraphService.ofNullable(entry.getUri(), true);
                if (opengraph.isPresent()){
                    description = opengraph.get().getContent("description");
                    imageUrl = opengraph.get().getContent("image");
                }
                return Content.builder()
                    .title(entry.getTitle())
                    .description(description)
                    .input(entry.getUri())
                    .imageUrl(imageUrl)
                    .order(i.incrementAndGet())
                    .userId(content.userId())
                    .parentId(content.id())
                    .type(ContentType.FEEDCONTENT)
                    .updated(ZonedDateTime.ofInstant(entry.getPublishedDate().toInstant(), ZoneId.systemDefault()))
                    .build();
            }).filter(Objects::nonNull).toList();
            
            contentRepository.saveAll(feedContents);
            if (feedContents.size()> 0){
                timelineIds.add(timeline.getKey());
            }
        }
        return new PullFeedDto(timelineIds);
    }
}
