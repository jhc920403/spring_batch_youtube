package com.crawling.youtube.core.domain;


import com.crawling.youtube.core.domain.embedded.YoutubeColumn;
import com.crawling.youtube.core.dto.YoutubeVideoDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "YOUTUBE_VIDEO_SEQ_GEN"           //시퀀스 제너레이터 이름
        , sequenceName = "YOUTUBE_VIDEO_SEQ"     //시퀀스 이름
        , initialValue = 1                       //시작값
        , allocationSize = 50                    //메모리를 통해 할당할 범위 사이즈
)
@Table(name = "YOUTUBE_VIDEO")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YoutubeViedo {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "YOUTUBE_VIDEO_SEQ_GEN"
    )
    @Column(name = "VIDEO_SEQ")
    private Long id;
    @Setter
    @Column(name = "VIDEO_ID")
    private String videoId;

    @Column(name = "UPLOAD_ID")
    private String uploadId;

    @Setter
    @Column(name = "TITLE")
    private String title;
    @Setter @Lob
    @Column(name = "DESCRIPT")
    private String descript;
    @Embedded
    private YoutubeColumn youtubeColumn;

    public static YoutubeViedo of(
            YoutubeVideoDto dto
    ) {
        YoutubeViedo youtubeViedo = new YoutubeViedo();
        youtubeViedo.videoId = dto.getVideoId();
        youtubeViedo.uploadId = dto.getUploadId();
        youtubeViedo.title = dto.getTitle();
        youtubeViedo.descript = dto.getDescript();
        youtubeViedo.youtubeColumn = new YoutubeColumn(
                dto.getThumbnailDefault()
                , dto.getThumbnailMedium()
                , dto.getThumbnailHigh()
                , dto.getPublishedAt()
        );

        return youtubeViedo;
    }
}
