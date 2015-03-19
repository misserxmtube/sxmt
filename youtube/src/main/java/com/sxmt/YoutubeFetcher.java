package com.sxmt;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.sun.deploy.util.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by douglas.calderon on 3/13/2015.
 */
public class YoutubeFetcher
{
    private static final String PROPERTIES_FILENAME = "youtube.properties";
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
    private static String API_KEY;

    private static YouTube youtube;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     */

    static {
        // Read the developer key from the properties file.
        Properties properties = new Properties();
        try {
            InputStream in = YoutubeFetcher.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);
            API_KEY = properties.getProperty("youtube.apikey");

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
        }

        // This object is used to make YouTube Data API requests. The last
        // argument is required, but since we don't need anything
        // initialized when the HttpRequest is initialized, we override
        // the interface and provide a no-op function.
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                new HttpRequestInitializer() {
                    public void initialize(HttpRequest request) throws IOException {}
                }
        ).setApplicationName("SXMT").build();
    }

//    private YoutubeFetcher() {}

    public static YoutubeRecord getYoutubeRecord(String song, String artist, boolean safeSearch, List<String> keys){
        StringBuilder sb = new StringBuilder();
        sb.append(song).append(" ").append(artist);
        for(String key : keys){
            sb.append(" ").append(key);
        }

        return getBestVideo(fetchSearchResults(sb.toString(), safeSearch), song, artist);
    }

    public static YoutubeRecord getYoutubeRecord(String song, String artist){
        String query = song+" "+artist;
        return getBestVideo(fetchSearchResults(query, false), song, artist);
    }

    protected static YoutubeRecord getBestVideo(List<SearchResult> searchResults, String song, String artist){
        prettyPrint(searchResults.iterator(), song, artist);

        LinkedHashMap<String, YoutubeRecord> youtubeRecordMap = new LinkedHashMap<>();
        for(SearchResult searchResult : searchResults){
            youtubeRecordMap.put(searchResult.getId().getVideoId(), searchResultToYoutubeRecord(searchResult));
        }
        List<String> videoIdList = new ArrayList<>(youtubeRecordMap.keySet()); // preserves order
        List<Video> videoList = fetchVideoResults(videoIdList);

        for(Video video : videoList){
            Period period = ISOPeriodFormat.standard().parsePeriod(video.getContentDetails().getDuration());
            youtubeRecordMap.get(video.getId()).setDuration(period.toStandardSeconds().getSeconds());

            if(video.getContentDetails().containsKey("regionRestriction")){
                VideoContentDetailsRegionRestriction restrictions = video.getContentDetails().getRegionRestriction();
                if(restrictions.containsKey("allowed") && !restrictions.getAllowed().contains("US")){
                    youtubeRecordMap.remove(video.getId());
                }
                if(restrictions.containsKey("blocked") && restrictions.getBlocked().contains("US")){
                    youtubeRecordMap.remove(video.getId());
                }
            }
        }

        YoutubeRecord result = youtubeRecordMap.get(new ArrayList<>(youtubeRecordMap.keySet()).get(0));
        prettyPrint(result);
        return result;
    }

    protected static YoutubeRecord searchResultToYoutubeRecord(SearchResult result){
        DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(result.getSnippet().getPublishedAt().toString());
        return new YoutubeRecord(
                result.getId().getVideoId(),
                result.getSnippet().getTitle(),
                result.getSnippet().getChannelTitle(),
                dt,
                result.getSnippet().getThumbnails().getDefault().getUrl()
        );
    }

    protected static List<SearchResult> fetchSearchResults(String query, boolean safe){
        YouTube.Search.List search = null;
        List<SearchResult> searchResultList = new LinkedList<SearchResult>();
        try
        {
            // Initialize Search.List call with 'parts' attribute
            search = youtube.search().list("id,snippet");

            search.setKey(API_KEY);
            search.setQ(query);
            search.setType("video");
            search.setVideoEmbeddable("true");
            if(safe){
                search.setSafeSearch("strict");
            }

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/videoId,snippet/title,snippet/channelTitle,snippet/publishedAt,snippet/thumbnails/default)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            searchResultList = searchResponse.getItems();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return searchResultList;
    }

    protected static List<Video> fetchVideoResults(List<String> songIds){
        YouTube.Videos.List videoSearch = null;
        List<Video> videoResultsList = new LinkedList<Video>();
        try{
            videoSearch = youtube.videos().list("id,contentDetails");

            videoSearch.setKey(API_KEY);
            StringUtils.join(songIds, ",");
            videoSearch.setId(StringUtils.join(songIds,","));
            videoSearch.setFields("items(id,contentDetails/regionRestriction,contentDetails/duration)");

            VideoListResponse videoResponse = videoSearch.execute();
            videoResultsList = videoResponse.getItems();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return videoResultsList;
    }

    protected static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String song, String artist) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on Song: \"" + song + "\" by \"" + artist + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();


            System.out.println(" Video Id: " + rId.getVideoId());
            System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
            System.out.println(" ChannelTitle: " + singleVideo.getSnippet().getChannelTitle());
            System.out.println(" PublishDate: " + singleVideo.getSnippet().getPublishedAt().toString());
            System.out.println(" Thumbnail: " + singleVideo.getSnippet().getThumbnails().getDefault().getUrl());
            System.out.println("\n-------------------------------------------------------------\n");
        }
    }

    protected static void prettyPrint(YoutubeRecord result){
        System.out.println( "*****************************************");
        System.out.println( "FOUND DA BEST WON DAWG");
        System.out.println( "Title        : "+result.getTitle() );
        System.out.println( "VideoId:     : "+result.getVideoId() );
        System.out.println( "ChannelTitle : "+result.getChannelTitle() );
        System.out.println( "PublishDate  : "+result.getPublishDate().toString() );
        System.out.println( "Duration(sec): "+result.getDuration() );
        System.out.println( "Thumbnail    : "+result.getThumbnail() );
        System.out.println( "*****************************************");
    }

}
