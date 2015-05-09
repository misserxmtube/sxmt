package com.sxmt.twitter;

import com.sxmt.twitter.dialects.GenericDialect;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * Created by remo.fischione on 3/14/2015.
 */
public class RunTweetConsumer
{
    private static final String PROPERTIES_FILENAME = "twitter.properties";
	public static void main(String[] args) throws Exception
	{
        Properties props = new Properties();
        try {
            InputStream in = RunTweetConsumer.class.getResourceAsStream("/"+PROPERTIES_FILENAME);
            props.load(in);
            in.close();
        }catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
        }
        List<String> stations = Arrays.asList(StringUtils.split(props.getProperty("twitter.stations"), ","));
        TweetConsumer consumer = new TweetConsumer();
        for(String station : stations){
            String stationHandle = props.getProperty("twitter.station." + station + ".handle");
            String stationRegex = props.getProperty("twitter.station." + station + ".regex");
            String stationExcludes = props.getProperty("twitter.station."+station+".excludes", "");
            GenericDialect stationDialect = new GenericDialect(stationRegex, Arrays.asList(StringUtils.split(stationExcludes,",")));
            consumer.addUser(stationHandle, stationDialect);
        }
        consumer.startConsuming();
//		TweetConsumer consumer = new TweetConsumer();
//		consumer.addUser("bpm_playlist", new BPMDialect());
//		consumer.startConsuming();

//		Thread.sleep(1000 * 20);
//
//		consumer.stopConsuming();
	}
}