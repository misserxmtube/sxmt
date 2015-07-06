package com.sxmt.metadata;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.util.Commander;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by douglas.calderon on 5/9/2015.
 */
public class EchonestFetcher
{
    private static final String PROPERTIES_FILENAME = "metadata.properties";
    private static String API_KEY;
    private static EchoNestAPI echoNest;
    private static Commander cmd = new Commander("EchoNestAPI");

    static {
        // Read the developer key from the properties file.
        Properties properties = new Properties();
        try {
            InputStream in = EchonestFetcher.class.getResourceAsStream("/" + PROPERTIES_FILENAME);
            properties.load(in);
            API_KEY = properties.getProperty("echonest.apikey");
        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
        }
        echoNest = new EchoNestAPI(API_KEY);
        Params stdParams = new Params();
        stdParams.add("api_key", API_KEY);
        cmd.setStandardParams(stdParams);
    }

    public static List<String> getGenres(String title, String artist) {
        Song song = getSong(title, artist);
        List<String> emptyList = new ArrayList<>();
        if(song != null){
            try {
                return getGenresByArtistId(song.getArtistID());
            }catch(EchoNestException e){
                e.printStackTrace();
                return emptyList;
            }
        }else{
            return emptyList;
        }
    }

    protected static Song getSong(String title, String artist){
        Params p = new Params();
        p.add("artist", artist);
        p.add("title", title);
        p.add("results", 1);
        Song song = null;
        try {
            List<Song> songs = echoNest.searchSongs(p);
            if(!songs.isEmpty()){
                song = songs.get(0);
            }else{
                System.err.println("No EchoNest record found. Title:"+title+" Artist:"+artist);
            }
        } catch(EchoNestException e){
            System.err.println("Error fetching song data from Echonest. Title:"+title+" Artist:"+artist);
        }

        return song;
    }

    protected static List<String> getGenresByArtistId(String artistId) throws EchoNestException{
        List<String> genres = new ArrayList<>();
        Params p = new Params();
        p.add("id",artistId);
        p.add("bucket","genre");

        Map results = cmd.sendCommand("artist/profile", p);
        Map response = (Map) results.get("response");
        Map artist = (Map) response.get("artist");
        List genreList = (List) artist.get("genres");
        for(Object genreMap : genreList){
            String genre = (String)((Map) genreMap).get("name");
            genres.add(genre);
        }

        return genres;
    }
}
