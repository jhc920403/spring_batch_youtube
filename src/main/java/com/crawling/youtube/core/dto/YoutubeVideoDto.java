package com.crawling.youtube.core.dto;

import com.crawling.youtube.core.domain.YoutubeChannel;
import com.google.api.services.youtube.model.PlaylistItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Builder
@Getter @Setter
public class YoutubeVideoDto {

    private String videoId;
    private String uploadId;
    private String title;
    private String descript;
    private String thumbnailDefault;
    private String thumbnailMedium;
    private String thumbnailHigh;
    private String useYn;
    private LocalDateTime publishedAt;
    private String status;

    public static YoutubeVideoDto toYoutubeVideoDto(PlaylistItem video, YoutubeChannel channel) {
        String pubTimeStr = video.getSnippet().getPublishedAt().toString();

        return YoutubeVideoDto.builder()
                .videoId(video.getContentDetails().getVideoId())
                .uploadId(channel.getUploadId())
                .title(video.getSnippet().getTitle())
                .descript(video.getSnippet().getDescription())
                .thumbnailDefault(!video.getSnippet().getThumbnails().containsKey("default") ?
                        "" : video.getSnippet().getThumbnails().getDefault().getUrl())
                .thumbnailMedium(!video.getSnippet().getThumbnails().containsKey("medium") ?
                        "" : video.getSnippet().getThumbnails().getMedium().getUrl())
                .thumbnailHigh(!video.getSnippet().getThumbnails().containsKey("high") ?
                        "" : video.getSnippet().getThumbnails().getHigh().getUrl())
                .publishedAt(LocalDateTime.parse(pubTimeStr.substring(0, pubTimeStr.indexOf('Z')-1)))
                .status(video.getStatus().getPrivacyStatus())
                .build();
    }
}
