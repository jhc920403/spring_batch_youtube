package com.crawling.youtube.batch.reader;

import com.crawling.youtube.core.domain.YoutubeChannel;
import com.crawling.youtube.core.dto.YoutubeVideoDto;
import com.crawling.youtube.core.repository.YoutubeVideoRepository;
import com.crawling.youtube.core.service.YoutubeApiService;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class YoutubeCrawlingGetPlaylistItemsReader implements ItemReader {

    private YoutubeApiService youtubeApiService;
    private YoutubeVideoRepository youtubeVideoRepository;
    private List<YoutubeChannel> channels;
    private YoutubeChannel channel;
    private List<PlaylistItem> playlistItems;
    private String nextPageToken;

    public YoutubeCrawlingGetPlaylistItemsReader(
            YoutubeApiService youtubeApiService
            , YoutubeVideoRepository youtubeVideoRepository
            , List<YoutubeChannel> channels
    ) {
        this.youtubeApiService = youtubeApiService;
        this.youtubeVideoRepository = youtubeVideoRepository;
        this.channels = channels;

        youtubeVideoRepository.deleteByChannelId(channels.stream().map(n->n.getUploadId()).collect(Collectors.toList()));
    }

    @Override
    public Object read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        // PlaylistItem 배열 내에 값이 존재하는 경우
        while (playlistItems == null || playlistItems.size() == 0) {

            if ((channels == null || channels.size() == 0) && nextPageToken == null) {
                return null;
            } else if (nextPageToken == null) {
                channel = channels.remove(0);
            }

            PlaylistItemListResponse playlistItemResult = youtubeApiService.getYoutubeVideoInfo(channel.getUploadId(), nextPageToken);
            playlistItems = playlistItemResult.getItems();
            nextPageToken = playlistItemResult.getNextPageToken();

            log.info("channelResult ::: " + channel);
            log.info("playlistItemResult ::: " + playlistItemResult);
        }

        return YoutubeVideoDto.toYoutubeVideoDto(playlistItems.remove(0), channel);
    }
}
