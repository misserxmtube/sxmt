package com.sxmt;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.sxmt.youtube.YoutubeRecord;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by douglas.calderon on 3/13/2015.
 */
public class YoutubeFetcher
{
    private static final String PROPERTIES_FILENAME = "youtube.properties";
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;
//    private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("YYYY-MM-DDTHH:mm:ss.SZ");
    private static String API_KEY;

    private YouTube youtube;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     */

    public YoutubeFetcher() {
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
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("SXMT").build();

    }

    public YoutubeRecord getYoutubeRecord(String song, String artist){
        String query = song+" "+artist;
        List<SearchResult> results = fetchResults(query);
        prettyPrint(results.iterator(), query);
        //initial naive 'get first'
        return searchResultToYoutubeRecord(results.get(0));
    }

    private YoutubeRecord searchResultToYoutubeRecord(SearchResult result){
        DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(result.getSnippet().getPublishedAt().toString());
        return new YoutubeRecord(
                result.getId().getVideoId(),
                result.getSnippet().getTitle(),
                result.getSnippet().getChannelTitle(),
                dt
        );
    }

    private List<SearchResult> fetchResults(String query){
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

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/videoId,snippet/title,snippet/channelTitle,snippet/publishedAt)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            searchResultList = searchResponse.getItems();
//            if (searchResultList != null) {
//                prettyPrint(searchResultList.iterator(), query);
//            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return searchResultList;
    }

    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
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
            System.out.println("\n-------------------------------------------------------------\n");
        }
    }

}
