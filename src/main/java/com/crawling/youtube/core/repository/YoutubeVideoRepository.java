package com.crawling.youtube.core.repository;

import com.crawling.youtube.core.domain.YoutubeViedo;
import com.crawling.youtube.core.dto.YoutubeVideoDto;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class YoutubeVideoRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(readOnly = false)
    public void save(YoutubeVideoDto dto) {
        if ("public".equals(dto.getStatus())) {
            entityManager.persist(YoutubeViedo.of(dto));
        }
    }

    @Transactional(readOnly = false)
    public void deleteByChannelId(List<String> uploadId) {
        entityManager.createQuery(
                "delete from YoutubeViedo y where y.uploadId in :uploadId"
                ).setParameter("uploadId", uploadId)
                .executeUpdate();
    }
}
