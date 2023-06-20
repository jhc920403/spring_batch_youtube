package com.crawling.youtube.core.dto;

import com.crawling.youtube.constant.YoutubeIdType;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Playlist;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Builder
@Getter @Setter
public class YoutubeChannelDto {

    private String channelId;
    private String title;
    private String descript;
    private String uploadId;
    private YoutubeIdType idType;
    private String thumbnailDefault;
    private String thumbnailMedium;
    private String thumbnailHigh;
    private LocalDateTime publishedAt;

    public static YoutubeChannelDto toChannelDto(Channel channel, YoutubeIdType type) {
        String pubTimeStr = channel.getSnippet().getPublishedAt().toString();

        return YoutubeChannelDto.builder()
                .channelId(channel.getId())
                .title(channel.getSnippet().getTitle())
                .descript(channel.getSnippet().getDescription())
                .uploadId(channel.getContentDetails().getRelatedPlaylists().getUploads())
                .idType(type)
                .thumbnailDefault(channel.getSnippet().getThumbnails().getDefault().getUrl())
                .thumbnailMedium(channel.getSnippet().getThumbnails().getMedium().getUrl())
                .thumbnailHigh(channel.getSnippet().getThumbnails().getHigh().getUrl())
                .publishedAt(LocalDateTime.parse(pubTimeStr.substring(0, pubTimeStr.indexOf('Z')-1)))
                .build();
    }

    public static YoutubeChannelDto toChannelDto(Playlist playlist, YoutubeIdType type) {
        String pubTiemStr = playlist.getSnippet().getPublishedAt().toString();

        return YoutubeChannelDto.builder()
                .channelId(playlist.getSnippet().getChannelId())
                .title(playlist.getSnippet().getTitle())
                .descript(playlist.getSnippet().getDescription())
                .uploadId(playlist.getId())
                .idType(type)
                .thumbnailDefault(playlist.getSnippet().getThumbnails().getDefault().getUrl())
                .thumbnailMedium(playlist.getSnippet().getThumbnails().getMedium().getUrl())
                .thumbnailHigh(playlist.getSnippet().getThumbnails().getHigh().getUrl())
                .publishedAt(LocalDateTime.parse(pubTiemStr.substring(0, pubTiemStr.indexOf('Z')-1)))
                .build();
    }
}
