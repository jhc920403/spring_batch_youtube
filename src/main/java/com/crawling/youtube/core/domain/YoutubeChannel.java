package com.crawling.youtube.core.domain;

import com.crawling.youtube.constant.YoutubeIdType;
import com.crawling.youtube.core.domain.embedded.YoutubeColumn;
import com.crawling.youtube.core.dto.YoutubeChannelDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "YOUTUBE_CHANNEL_SEQ_GEN"           //시퀀스 제너레이터 이름
        , sequenceName = "YOUTUBE_CHANNEL_SEQ"     //시퀀스 이름
        , initialValue = 1                         //시작값
        , allocationSize = 50                      //메모리를 통해 할당할 범위 사이즈
)
@Table(name = "YOUTUBE_CHANNEL")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YoutubeChannel {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE          //사용할 전략을 시퀀스로 선택
            , generator = "YOUTUBE_CHANNEL_SEQ_GEN"     //식별자 생성기를 설정해놓은 YOUTUBE_CHANNEL_SEQ_GEN 으로 설정
    )
    @Column(name = "CHANNEL_SEQ")
    private Long id;

    @Column(name = "CHANNEL_ID")
    private String channelId;
    @Setter
    @Column(name = "TITLE")
    private String title;
    @Setter
    @Column(name = "DESCRIPT")
    private String descript;

    @Column(name = "UPLOAD_ID")
    private String uploadId;
    @Column(name = "ID_TYPE")
    @Enumerated(EnumType.STRING)
    private YoutubeIdType idType;

    @Embedded
    private YoutubeColumn youtubeColumn;

    public static YoutubeChannel of(
            YoutubeChannelDto dto
    ) {
        YoutubeChannel youtubeChannel = new YoutubeChannel();
        youtubeChannel.channelId = dto.getChannelId();
        youtubeChannel.title = dto.getTitle();
        youtubeChannel.descript = dto.getDescript();
        youtubeChannel.uploadId = dto.getUploadId();
        youtubeChannel.idType = dto.getIdType();
        youtubeChannel.youtubeColumn = new YoutubeColumn(
                dto.getThumbnailDefault()
                , dto.getThumbnailMedium()
                , dto.getThumbnailHigh()
                , dto.getPublishedAt()
        );

        return youtubeChannel;
    }
}
