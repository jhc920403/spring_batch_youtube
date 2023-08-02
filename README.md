#  🛠 Youtube Crawling

Youtube Data API v3를 사용하여 Channel ID를 기준으로 해당 채널의 동영상 정보를 수집해오는 기능을 수행하는 배치입니다. (스프링 배치 사용/학습 목적)

<br><br>

##  🎯 실행 명령어
- [Channel ID]: 다수의 채널 ID를 받을 수 있으며, 구분자는 `,`(쉼표)로 작성한다.<br>
  `--spring.batch.job.names=youtubeCrawlingChannelIdJob channelId=[Channel ID]`

<br><br>

##  🔗 Batch Properties(yml 파일)
```yml
spring:
  batch:
    job:
      names: ${job.name:NONE}

---

spring:
  datasource:
    url: jdbc:oracle:thin:@[ORACLE DATABASE IP]:[ORACLE DATABASE PORT]:[ORACLE DATABASE SID]
    username: [username]
    password: [password]
    driver-class-name: oracle.jdbc.OracleDriver

  jpa:
    database: oracle
    database-platform: org.hibernate.dialect.Oracle12cDialect
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
    open-in-view: false

  batch:
    jdbc:
      initialize-schema: always

youtube:
  api-key: [YOUTUBE API KEY]

logging:
  level:
    sql: debug
```

<br><br>

##  ⚙ 사용한 기능
  1. `Spring Boot:2.7.12`
  2. `Spring Batch:4.3.8`
  3. `Spring Data JPA:2.7.12`
  4. `Oracle 19c`

<br><br>

##  📆 코드 작성 사항
  1. `Spring Batch` 를 사용한 Youtube Channel / 영상정보 획득 ✔
  2. `Spring Data JPA` 를 사용한 데이터 저장
     1. `JPA` 기본적인 기능을 사용하여 데이터베이스 조작 ✔
     2. `Spring Data JPA` 의 Repository를 구현하여 데이터베이스 조작 ✖
  3. `Junit` 을 사용하여 Test 코드 작성 ✖

<br><br>

##  🚫 방화벽 오픈 정보
Google API 정보는 특정 IP가 아닌 DNS 기반으로 수집되기 때문에 `nslookup`으로 조회한 IP도 변동이 생겨 수집에 문제가 발생한다.
<br>문제를 해결할 수 있는 방법은 IP 대역을 알아내거나 DNS를 사용하여 API 통신하면 된다.
<br>다만, 실무에서는 외부망과 통신은 제한적으로만 오픈되기 때문에 오픈 정보가 필요하며, 구현하면서 획득한 정보이다.

🌐 DNS 정보 : `https://www.googleapis.com/`<br>Youtube에서 제공하는 `Lib`를 사용하는 경우 `google-api-services-youtube-v3-[version].jar` > `Youtube.class` 경로 내에서 확인할 수 있다.<br>
🌐 IP 대역 정보 : `https://support.google.com/a/answer/10026322?hl=en`에서 CIDR 형태로 확인 가능<br>
🌐 DNS/IP PORT 정보 : 443

<br><br>

##  📚 Youtube Data API Lib
Youtube Lib는 Java1.8 이상부터 사용할 수 있다는 제약이 있다.
- `google-api-client-[version].jar`
- `google-api-services-youtube-v3-[version].jar`
- `google-http-client-[version].jar`
- `google-http-client-jackson2-[version].jar`
- `guava-jdk5-[version].jar`
- `jackson-core-[version].jar`

<br><br>

##  💾 데이터베이스 테이블 정보 
![image](https://github.com/jhc920403/spring_batch_youtube/assets/135422171/db70a747-1389-435a-baf3-801a063ccfc4)

### 이모지 설정
- Youtube를 수집하다보면 이모지 텍스트도 종종 발견할 수 있으며, 정상적으로 수집하기 위해서는 문자 셋팅을 수정해야된다.
  ```sql
  update props$ set value$='KOREAN' where name='NLS_LANGUAGE';
  update props$ set value$='KOREA' where name='NLS_TERRITORY';
  update props$ set value$='AL32UTF8' where name='NLS_CHARACTERSET';
  update props$ set value$='AL16UTF16' where name='NLS_NCHAR_CHARACTERSET';
  
  SHUTDOWN IMMEDIATE;
  STARTUP MOUNT;
  ALTER SYSTEM ENABLE RESTRICTED SESSION;
  ALTER SYSTEM SET JOB_QUEUE_PROCESSES=0;
  ALTER SYSTEM SET AQ_TM_PROCESSES=0;
  ALTER DATABASE OPEN;
  col value new_value charset;
  SELECT VALUE FROM NLS_DATABASE_PARAMETERS WHERE PARAMETER='NLS_CHARACTERSET';
  col value new_value ncharset
  SELECT VALUE FROM NLS_DATABASE_PARAMETERS WHERE PARAMETER = 'NLS_NCHAR_CHARACTERSET'; 
  ALTER DATABASE CHARACTER SET INTERNAL_USE & CHARSET;
  ALTER DATABASE NATIONAL CHARACTER SET INTERNAL_USE & NCHARSET; 
  SHUTDOWN IMMEDIATE;
  STARTUP;
  SHUTDOWN IMMEDIATE;
  STARTUP;
  ```

<br>

### 테이블은 2개로 구성
- `YOUTUBE_CHANNEL`  : Youtube Channel의 동영상 업로드/재생목록 정보를 저장
  - 테이블 생성 DDL
    ```sql
    -- 테이블 생성
    CREATE TABLE YOUTUBE_CHANNEL (
        CHANNEL_SEQ             NUMBER(19,0) NOT NULL
        , CHANNEL_ID            VARCHAR2(48 CHAR)
        , TITLE                 VARCHAR2(200 CHAR)
        , DESCRIPT              VARCHAR2(500 CHAR)
        , UPLOAD_ID             VARCHAR2(100 CHAR)
        , ID_TYPE               VARCHAR2(15 CHAR)
        , THUMBNAIL_DEFAULT     VARCHAR(600 CHAR)
        , THUMBNAIL_MEDIUM      VARCHAR(600 CHAR)
        , THUMBNAIL_HIGH        VARCHAR(600 CHAR)
        , USE_YN                VARCHAR(1 CHAR)
        , PUBLISHED_AT          TIMESTAMP
        , CREATE_DATE           TIMESTAMP NOT NULL
        , MODIFIED_DATE         TIMESTAMP NOT NULL
    );

    -- 테이블 시퀀스 생성
    CREATE SEQUENCE YOUTUBE_CHANNEL_SEQ START WITH 1 INCREMENT BY 50;

    -- 테이블 인덱스 설정
    CREATE UNIQUE INDEX YOUTUBE_CHANNEL_UNIQUE_INDEX ON YOUTUBE_CHANNEL(CHANNEL_ID, UPLOAD_ID, ID_TYPE);

    -- 테이블 PK 설정
    ALTER TABLE YOUTUBE_CHANNEL ADD CONSTRAINT YOUTUBE_CHANNEL_PRIMARY_KEY PRIMARY KEY(CHANNEL_SEQ);

    -- 테이블 각 필드에 대한 COMMENT
    COMMENT ON TABLE YOUTUBE_CHANNEL IS 'YOUTUBE CHANNEL의 UPLOAD/PLAYLIST ID 정보';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.CHANNEL_SEQ IS 'YOUTUBE CHANNEL TABLE의 SEQ 정보';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.CHANNEL_ID IS 'YOUTUBE CHANNEL ID 정보';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.TITLE IS 'YOUTUBE CHANNEL/PLAYLIST NAME';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.DESCRIPT IS 'YOUTUBE CHANNEL/PLAYLIST에 대한 설명';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.UPLOAD_ID IS 'YOUTUBE CHANNEL의 CHANNEL/PLAYLIST ID 정보';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.ID_TYPE IS 'YOUTUBE CHANNEL인지 PLAYLIST인지 ID TYPE 구부자';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.THUMBNAIL_DEFAULT IS 'YOUTUBE CHANNEL의 THUMBNAIL';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.THUMBNAIL_MEDIUM IS 'YOUTUBE CHANNEL의 THUMBNAIL';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.THUMBNAIL_HIGH IS 'YOUTUBE CHANNEL의 THUMBNAIL';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.USE_YN IS 'YOUTUBE CHANNEL의 사용 활성화 여부';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.PUBLISHED_AT IS 'YOUTUBE CHANNEL/PLAYLIST 배포 일자';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.CREATE_DATE IS 'YOUTUBE CHANNEL TABLE의 COLUMN 등록 일자';
    COMMENT ON COLUMN YOUTUBE_CHANNEL.MODIFIED_DATE IS 'YOUTUBE CHANNEL TABLE의 COLUMN 수정 일자';
    ```
- `YOUTUBE_VIDEO`    : Youtube Channel의 동영상 업로드/재생목록 정보를 기반으로 수집한 실질적인 동영상 정보 저장
  - 테이블 생성 DDL
    ```sql
    -- 테이블 생성
    CREATE TABLE YOUTUBE_VIDEO (
        VIDEO_SEQ            NUMBER(19,0)
        , VIDEO_ID           VARCHAR(100 CHAR)
        , UPLOAD_ID          VARCHAR2(100 CHAR)
        , TITLE              VARCHAR2(200 CHAR) 
        , DESCRIPT           CLOB
        , THUMBNAIL_DEFAULT  VARCHAR(600 CHAR)
        , THUMBNAIL_MEDIUM   VARCHAR(600 CHAR)
        , THUMBNAIL_HIGH     VARCHAR(600 CHAR)
        , USE_YN             VARCHAR(1 CHAR) DEFAULT 'Y'
        , PUBLISHED_AT       TIMESTAMP
        , CREATE_DATE        TIMESTAMP NOT NULL
        , MODIFIED_DATE      TIMESTAMP NOT NULL
    );

    -- 테이블 시퀀스 생성
    CREATE SEQUENCE YOUTUBE_VIDEO_SEQ START WITH 1 INCREMENT BY 50;

    -- 테이블 PK 설정
    ALTER TABLE YOUTUBE_VIDEO ADD CONSTRAINT YOUTUBE_VIDEO_PRIMARY_KEY PRIMARY KEY(VIDEO_SEQ);

    -- 테이블 각 필드에 대한 COMMENT
    COMMENT ON TABLE YOUTUBE_VIDEO IS 'YOUTUBE VIDEO의 정보';
    COMMENT ON COLUMN YOUTUBE_VIDEO.VIDEO_SEQ IS 'YOUTUBE VIDEO TABLE의 SEQ';
    COMMENT ON COLUMN YOUTUBE_VIDEO.VIDEO_ID IS 'YOUTUBE VIDEO ID 정보';
    COMMENT ON COLUMN YOUTUBE_VIDEO.UPLOAD_ID IS 'YOUTUBE CHANNEL의 UPLOAD ID 정보';
    COMMENT ON COLUMN YOUTUBE_VIDEO.TITLE IS 'YOUTUBE VIDEO 제목(타이틀)';
    COMMENT ON COLUMN YOUTUBE_VIDEO.DESCRIPT IS 'YOUTUBE VIDEO 설명';
    COMMENT ON COLUMN YOUTUBE_VIDEO.THUMBNAIL_DEFAULT IS 'YOUTUBE VIDEO의 THUMBNAIL';
    COMMENT ON COLUMN YOUTUBE_VIDEO.THUMBNAIL_MEDIUM IS 'YOUTUBE VIDEO의 THUMBNAIL';
    COMMENT ON COLUMN YOUTUBE_VIDEO.THUMBNAIL_HIGH IS 'YOUTUBE VIDEO의 THUMBNAIL';
    COMMENT ON COLUMN YOUTUBE_VIDEO.USE_YN IS 'YOUTUBE VIDEO의 사용 활성화 여부';
    COMMENT ON COLUMN YOUTUBE_VIDEO.PUBLISHED_AT IS 'YOUTUBE VIDEO 등록 일자';
    COMMENT ON COLUMN YOUTUBE_VIDEO.CREATE_DATE IS 'YOUTUBE VIDEO TABLE의 COLUMN 등록 일자';
    COMMENT ON COLUMN YOUTUBE_VIDEO.MODIFIED_DATE IS 'YOUTUBE VIDEO TABLE의 COLUMN 수정 일자';    
    ```
