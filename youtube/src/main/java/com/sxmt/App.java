package com.sxmt;

import com.sxmt.youtube.YoutubeRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Hello world!
 *
 */
public class App
{
    private static String getInputQuery()
    {
        String inputQuery = "";
        try
        {

            System.out.print("Please enter a search term: ");
            BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
            inputQuery = bReader.readLine();

            if (inputQuery.length() < 1)
            {
                // Use the string "YouTube Developers Live" as a default.
                inputQuery = "YouTube Developers Live";
            }
        }catch(IOException e){
            System.err.print(e);
        }
        return inputQuery;
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        YoutubeFetcher fetcher = new YoutubeFetcher();
        YoutubeRecord record = fetcher.getYoutubeRecord(getInputQuery(),"");

        System.out.println( "*****************************************");
        System.out.println( "FOUND DA BEST WON DAWG");
        System.out.println( "Title        :"+record.getTitle() );
        System.out.println( "VideoId:     :"+record.getVideoId() );
        System.out.println( "ChannelTitle :"+record.getChannelTitle() );
        System.out.println( "PublishDate  :"+record.getPublishDate().toString() );
        System.out.println( "*****************************************");
    }
}
