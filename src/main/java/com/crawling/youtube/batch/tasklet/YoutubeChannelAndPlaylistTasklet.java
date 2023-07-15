package com.crawling.youtube.batch.tasklet;

import com.crawling.youtube.core.domain.YoutubeChannel;
import com.crawling.youtube.core.repository.YoutubeChannelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class YoutubeChannelAndPlaylistTasklet implements Tasklet {

    private YoutubeChannelRepository youtubeChannelRepository;
    private ExecutionContext executionContext;
    private List<String> channelId;


    public YoutubeChannelAndPlaylistTasklet(
            YoutubeChannelRepository youtubeChannelRepository
            , String channelId
    ) {
        this.youtubeChannelRepository = youtubeChannelRepository;
        this.channelId = Arrays.asList(channelId.split(","));
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        initExecutionContext(chunkContext);

        saveYoutubeChannelPage(youtubeChannelRepository.findAll(channelId));
        contribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }

    private void initExecutionContext(ChunkContext chunkContext) {
        executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }

    private void saveYoutubeChannelPage(List<YoutubeChannel> channels) {
        executionContext.put("channels", channels);
        log.info("getChannel ::: " + channels.toString());
    }
}
