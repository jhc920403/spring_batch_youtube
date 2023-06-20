package com.crawling.youtube.core.repository;

import com.crawling.youtube.core.domain.YoutubeChannel;
import com.crawling.youtube.core.dto.YoutubeChannelDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class YoutubeChannelRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(readOnly = false)
    public void save(YoutubeChannelDto dto) {
        entityManager.persist(YoutubeChannel.of(dto));
    }

    @Transactional(readOnly = false)
    public void deleteByChannelId(String channelId) {
        List<String> channels = Arrays.asList(channelId.split(","));
        entityManager.createQuery("delete from YoutubeChannel y where y.channelId in :channelId")
                .setParameter("channelId" , channels)
                .executeUpdate();
    }

    public Long finalByChannelIdCount(String channelId) {
        return entityManager.createQuery(
                "select count(y) from YoutubeChannel y " +
                        "where y.channelId in :channelId"
                , Long.class
                ).setParameter("channelId", channelId)
                .getSingleResult();
    }

    public List<YoutubeChannel> findByChannelId(List<String> channelId, int offset, int limit) {
        return entityManager.createQuery(
                "select y from YoutubeChannel y " +
                        "where y.channelId in :channelId"
                        , YoutubeChannel.class
                ).setParameter("channelId", channelId)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<YoutubeChannel> findAll(List<String> channelId) {
        return entityManager.createQuery(
                "select y from YoutubeChannel y " +
                        "where y.channelId in :channelId"
                , YoutubeChannel.class
                ).setParameter("channelId", channelId)
                .getResultList();
    }
}
