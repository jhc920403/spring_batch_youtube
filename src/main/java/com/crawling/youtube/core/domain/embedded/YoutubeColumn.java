package com.crawling.youtube.core.domain.embedded;

import com.crawling.youtube.constant.YoutubeUseType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YoutubeColumn {
    @Setter
    @Column(name = "THUMBNAIL_DEFAULT")
    private String thumbnailDefault;
    @Setter
    @Column(name = "THUMBNAIL_MEDIUM")
    private String thumbnailMedium;
    @Setter
    @Column(name = "THUMBNAIL_HIGH")
    private String thumbnailHigh;
    @Setter
    @Column(name = "USE_YN")
    @Enumerated(EnumType.STRING)
    private YoutubeUseType useYn;
    @Column(name = "PUBLISHED_AT")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime publishedAt;
    @CreatedDate
    @Column(name = "CREATE_DATE")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createDate;
    @LastModifiedDate
    @Column(name = "MODIFIED_DATE")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedDate;

    public YoutubeColumn(
            String thumbnailDefault
            , String thumbnailMedium
            , String thumbnailHigh
            , LocalDateTime publishedAt
    ) {
        this.thumbnailDefault = thumbnailDefault;
        this.thumbnailMedium = thumbnailMedium;
        this.thumbnailHigh = thumbnailHigh;
        this.publishedAt = publishedAt;
    }
}
