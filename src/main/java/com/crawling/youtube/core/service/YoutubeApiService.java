package com.crawling.youtube.core.service;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeApiService {

    @Value("${youtube.api-key}")
    private String youtubeKey;

    /** Global instance of the HTTP transport. */
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private final JsonFactory JSON_FACTORY = new JacksonFactory();

    /** Global instance of the max number of videos we want returned (50 = upper limit per page). */
    private final long NUMBER_OF_VIDEOS_RETURNED = 50;

    /** Global instance of Youtube object to make all API requests. */
    private YouTube youtube;

    /**
     * ChannelId를 사용하여 채널의 UploadId를 조회하여 데이터베이스에 저장
     * @param channelId Youtube Channel Id
     */
    public List<Channel> getYoutubeChannelInfo(String channelId) {
        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {}
        }).setApplicationName("YoutubeAPI").build();

        List<Channel> channelInfo = null;
        try {
            YouTube.Channels.List channelRequest = youtube.channels().list("id,snippet,brandingSettings,contentDetails");

            channelRequest.setKey(youtubeKey);
            channelRequest.setId(channelId);
            channelRequest.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            channelRequest.setPart("id,snippet,brandingSettings,contentDetails");

            ChannelListResponse searchResponse = channelRequest.execute();
            channelInfo = searchResponse.getItems();
            channelInfo.stream().forEach(n -> log.info(n.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return channelInfo;
    }

    /**
     * ChannelId를 사용하여 채널의 PlayListId를 조회하여 데이터베이스에 저장
     * @param channelId Youtube Channel Id
     */
    public PlaylistListResponse getYoutubePlaylistInfo(String channelId, String nextPageToken) {

        PlaylistListResponse searchResponse = null;
        try {
            youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {}
            }).setApplicationName("").build();

            YouTube.Playlists.List playlistInfo = youtube.playlists().list("id,snippet,status");
            playlistInfo.setKey(youtubeKey);
            playlistInfo.setChannelId(channelId);
            playlistInfo.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            if (nextPageToken != null) playlistInfo.setPageToken(nextPageToken);

            searchResponse = playlistInfo.execute();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return searchResponse;
    }

    /**
     * UploadId / PlaylistId 를 사용하여 채널의 동영상 정보를 조회하여 데이터베이스에 저장
     */
    public PlaylistItemListResponse getYoutubeVideoInfo(String uploadId, String nextPageToken) {

        youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {}
        }).setApplicationName("YoutubeAPI").build();

        PlaylistItemListResponse playlistItemResult = null;
        try {
            YouTube.PlaylistItems.List playlistItemsRequest = youtube.playlistItems().list("id,snippet,contentDetails,status");

            playlistItemsRequest.setKey(youtubeKey);
            playlistItemsRequest.setPlaylistId(uploadId);
            playlistItemsRequest.setPart("id,snippet,contentDetails,status");
            playlistItemsRequest.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            if (nextPageToken != null) playlistItemsRequest.setPageToken(nextPageToken);

            playlistItemResult = playlistItemsRequest.execute();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return playlistItemResult;
    }
}
