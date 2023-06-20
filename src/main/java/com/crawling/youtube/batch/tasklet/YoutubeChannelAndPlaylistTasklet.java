package com.crawling.youtube.batch.tasklet;

import com.crawling.youtube.core.domain.YoutubeChannel;
import com.crawling.youtube.core.repository.YoutubeChannelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class YoutubeChannelAndPlaylistTasklet implements Tasklet {

    private YoutubeChannelRepository youtubeChannelRepository;
    private ExecutionContext executionContext;
    private List<String> channelId;
    private int offset = 0;
    private final int LIMIT = 50;
    private Long maxSize;

    public YoutubeChannelAndPlaylistTasklet(
            YoutubeChannelRepository youtubeChannelRepository
            , String channelId
            , List<String> publicStorage
    ) {
        this.youtubeChannelRepository = youtubeChannelRepository;
        this.channelId = Arrays.asList(channelId.split(","));

        maxSize = youtubeChannelRepository.finalByChannelIdCount(channelId);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        initExecutionContext(chunkContext);

        if (executionContext.containsKey("offset") && executionContext.getLong("offset") * 50 > maxSize) {
            contribution.setExitStatus(ExitStatus.COMPLETED);
            return RepeatStatus.FINISHED;
        }

        //saveYoutubeChannelPage(youtubeChannelRepository.findByChannelId(channelId, offset, LIMIT));
        saveYoutubeChannelPage(youtubeChannelRepository.findAll(channelId));
        contribution.setExitStatus(new ExitStatus("CONTINUABLE"));

        return RepeatStatus.FINISHED;
    }

    private void initExecutionContext(ChunkContext chunkContext) {
        executionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }

    private void saveYoutubeChannelPage(List<YoutubeChannel> channels) {
        executionContext.putLong("offset", offset * 50);
        executionContext.put("channels", channels);

        offset++;
        log.info("getChannel ::: " + channels.toString());
    }
}
