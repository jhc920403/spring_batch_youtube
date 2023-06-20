package com.crawling.youtube.batch.writer;

import com.crawling.youtube.batch.reader.YoutubeCrawlingGetPlaylistItemsReader;
import com.crawling.youtube.core.dto.YoutubeVideoDto;
import com.crawling.youtube.core.repository.YoutubeVideoRepository;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class YoutubeCrawlingGetPlaylistItemsWriter implements ItemWriter {

    private YoutubeVideoRepository youtubeVideoRepository;

    public YoutubeCrawlingGetPlaylistItemsWriter (
            YoutubeVideoRepository youtubeVideoRepository
    ) {
        this.youtubeVideoRepository = youtubeVideoRepository;
    }

    @Override
    public void write(List items) throws Exception {
        items.stream().forEach(n -> {
            youtubeVideoRepository.save((YoutubeVideoDto) n);
        });
    }
}
