package com.crawling.youtube.batch.tasklet;

import com.crawling.youtube.constant.YoutubeIdType;
import com.crawling.youtube.core.dto.YoutubeChannelDto;
import com.crawling.youtube.core.repository.YoutubeChannelRepository;
import com.crawling.youtube.core.service.YoutubeApiService;
import com.google.api.services.youtube.model.PlaylistListResponse;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class YoutubeChannelTasklet implements Tasklet {

    private YoutubeApiService youtubeApiService;
    private YoutubeChannelRepository youtubeChannelRepository;
    private String channelId;

    public YoutubeChannelTasklet (
            YoutubeApiService youtubeApiService
            , YoutubeChannelRepository youtubeChannelRepository
            , String channelId
    ) {
        this.youtubeApiService = youtubeApiService;
        this.youtubeChannelRepository = youtubeChannelRepository;
        this.channelId = channelId;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        youtubeChannelRepository.deleteByChannelId(channelId);

        // ChannelId를 통해 UploadId를 데이터베이스에 저장
        youtubeApiService.getYoutubeChannelInfo(channelId).stream().forEach(n -> {
            youtubeChannelRepository.save(YoutubeChannelDto.toChannelDto(n, YoutubeIdType.UPLOAD_ID));
        });

        // ChannelId를 통해 PlayList를 데이터베이스에 저장
        for (String id : channelId.split(",")) {
            String nextPageToken = null;

            do {
                PlaylistListResponse uploadList = youtubeApiService.getYoutubePlaylistInfo(id, nextPageToken);
                nextPageToken = uploadList.getNextPageToken();

                uploadList.getItems().stream().forEach(n -> {
                    youtubeChannelRepository.save(YoutubeChannelDto.toChannelDto(n, YoutubeIdType.PLAYLIST_ID));
                });
            } while (nextPageToken != null);
        }

        return RepeatStatus.FINISHED;
    }
}
