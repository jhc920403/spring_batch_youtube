package com.crawling.youtube.batch.job;

import com.crawling.youtube.batch.reader.YoutubeCrawlingGetPlaylistItemsReader;
import com.crawling.youtube.batch.tasklet.YoutubeChannelTasklet;
import com.crawling.youtube.batch.tasklet.YoutubeChannelAndPlaylistTasklet;
import com.crawling.youtube.batch.writer.YoutubeCrawlingGetPlaylistItemsWriter;
import com.crawling.youtube.core.domain.YoutubeChannel;
import com.crawling.youtube.core.repository.YoutubeChannelRepository;
import com.crawling.youtube.core.repository.YoutubeVideoRepository;
import com.crawling.youtube.core.service.YoutubeApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class YoutubeCrawlingChannelIdJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final YoutubeApiService youtubeApiService;
    private final YoutubeChannelRepository youtubeChannelRepository;

    @Bean
    public Job youtubeCrawlingChannelIdJob(
            Step youtubeCrawlingChannelIdStep
            , Step getVideoStep
    ) {
        return jobBuilderFactory.get("youtubeCrawlingChannelIdJob")
                .incrementer(new RunIdIncrementer())
                .start(youtubeCrawlingChannelIdStep)
                .next(getVideoStep)
                .build();
    }

    /**
     * YOTUBE 정보를 가져오기 위한 UPLOAD ID와 PLAYLIST ID를 수집하는 STEP
     */
    @Bean
    @JobScope
    public Step youtubeCrawlingChannelIdStep(
            Tasklet youtubeCrawlingChannelIdTasklet
    ) {
        return stepBuilderFactory.get("youtubeCrawlingChannelIdStep")
                .tasklet(youtubeCrawlingChannelIdTasklet)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet youtubeCrawlingChannelIdTasklet(
            YoutubeApiService youtubeApiService
            , YoutubeChannelRepository youtubeChannelRepository
            , @Value("#{jobParameters['channelId']}") String channelId
    ) {
        return new YoutubeChannelTasklet(
                youtubeApiService
                , youtubeChannelRepository
                , channelId
        );
    }

    /**
     * YOUTUBE VIDEO 정보를 가져오기 위한 STEP
     */
    @Bean
    public Step getVideoStep(
            Flow getVideoFlow
    ) {
        return stepBuilderFactory.get("getVideoStep")
                .flow(getVideoFlow)
                .build();
    }

    @Bean
    @JobScope
    public Flow getVideoFlow(
            Step youtubeCrawlingGetUploadIdAndPlayListIdStep
            , Step youtubeCrawlingGetPlaylistItemsStep
    ) {
        FlowBuilder<Flow> builder = new FlowBuilder<>("getVideoFlow");
        builder.start(youtubeCrawlingGetUploadIdAndPlayListIdStep)
                .on("*")
                .to(youtubeCrawlingGetPlaylistItemsStep)
                .on("*").end();
        return builder.build();
    }

    /**
     * 1. Tasklet으로 UPLOAD ID / PLAYLIST ID 한 개씩 조회
     */
    @Bean
    @JobScope
    public Step youtubeCrawlingGetUploadIdAndPlayListIdStep(
            Tasklet youtubeCrawlingGetUploadIdAndPlayListTasklet
    ) {
        return stepBuilderFactory.get("youtubeCrawlingGetUploadIdAndPlayListIdTasklet")
                .tasklet(youtubeCrawlingGetUploadIdAndPlayListTasklet)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet youtubeCrawlingGetUploadIdAndPlayListTasklet(
            YoutubeChannelRepository youtubeChannelRepository
            , @Value("#{jobParameters['channelId']}") String channelId
    ) {
        return new YoutubeChannelAndPlaylistTasklet(
                youtubeChannelRepository
                , channelId
        );
    }

    @Bean
    @JobScope
    public Step youtubeCrawlingGetPlaylistItemsStep(
            YoutubeCrawlingGetPlaylistItemsReader youtubeCrawlingGetPlaylistItemsReader
            , YoutubeCrawlingGetPlaylistItemsWriter youtubeCrawlingGetPlaylistItemsWriter
    ) {
        return stepBuilderFactory.get("youtubeCrawlingGetPlaylistItemsStep")
                .chunk(50)
                .reader(youtubeCrawlingGetPlaylistItemsReader)
                .writer(youtubeCrawlingGetPlaylistItemsWriter)
                .build();
    }

    @Bean
    @StepScope
    public YoutubeCrawlingGetPlaylistItemsReader youtubeCrawlingGetPlaylistItemsReader(
            YoutubeApiService youtubeApiService
            , YoutubeVideoRepository youtubeVideoRepository
            , @Value("#{jobExecutionContext['channels']}") List<YoutubeChannel> channels
    ) {
        return new YoutubeCrawlingGetPlaylistItemsReader(youtubeApiService, youtubeVideoRepository, channels);
    }

    @Bean
    @StepScope
    public YoutubeCrawlingGetPlaylistItemsWriter youtubeCrawlingGetPlaylistItemsWriter(
            YoutubeVideoRepository youtubeVideoRepository
    ) {
        return new YoutubeCrawlingGetPlaylistItemsWriter(youtubeVideoRepository);
    }
}
