package com.sxmt.twitter;

import com.sxmt.twitter.dialects.SXMDialect;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Reads tweets from a user stream.
 *
 * Created by rvfischione on 3/13/2015.
 */
public class UserStreamReader implements Runnable
{
	private final Logger LOG = LoggerFactory.getLogger(UserStreamReader.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final String consumerKey = "dQskdemPDk9E6AjgmpcIfMpqi";
	private final String consumerSecret = "TUkTc5aMsbZTPc8ndQ5UQDkWocYx5wR9ZSKHoJIEEdpZAQpuR4";
	private final String token = "3089407595-97kWtUYxO7WtL1oUL54xSdHGdY2G6Kt3YIIpe3Y";
	private final String secret = "JXUhfSdQII6qkv2bFSmR8FWyEhBvCSPHb0DgN7CPIvQDz";

	private Client hosebirdClient;
	private BlockingQueue<String> msgQueue;
	private BlockingQueue<Event> eventQueue;
	private Map<Long, SXMDialect> userIDs;

	public UserStreamReader()
	{
		userIDs = new HashMap<Long, SXMDialect>();

		/** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
		msgQueue = new LinkedBlockingQueue<String>(100000);
		eventQueue = new LinkedBlockingQueue<Event>(1000);
	}

	@Override
	public void run()
	{
		/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		List<Long> ids = new ArrayList<>(userIDs.keySet());
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint().followings(ids);
		hosebirdEndpoint.stallWarnings(false);
        hosebirdEndpoint.filterLevel(Constants.FilterLevel.Medium);

		// Optional: set up some followings and track terms
//		List<Long> followings = Lists.newArrayList(1234L, 566788L);
//		List<String> terms = Lists.newArrayList("twitter", "api");
//		hosebirdEndpoint.followings(followings);
//		hosebirdEndpoint.trackTerms(terms);

		// These secrets should be read from a config file
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

		// Set up a client:
		ClientBuilder builder = new ClientBuilder()
				.name("SXMT-Client-1")                              // optional: mainly for the logs
				.hosts(hosebirdHosts)
				.authentication(hosebirdAuth)
				.endpoint(hosebirdEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue))
				.eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

		hosebirdClient = builder.build();
		// Attempts to establish a connection.
		hosebirdClient.connect();

		// Do whatever needs to be done with messages
		while (!hosebirdClient.isDone())
		{
            try {
                String msg = "";
                try {
                    msg = msgQueue.poll(30, TimeUnit.SECONDS);
                } catch (Exception e)
                {
                    // interrupted while polling
    				LOG.error("Unexpected error while polling", e);
                    stop();
                }

                if (msg == null || msg.equals(""))
                {
                    System.out.println("Did not receive a message in 30 seconds");
                } else
                {
    //				System.out.println(msg);
                    JsonNode node;
                    try {
                        node = objectMapper.readTree(msg);
                    } catch (Exception e)
                    {
                        LOG.error("Exception while parsing response", e);
                        throw new RuntimeException(e);
                    }

                    buildAndStoreTweet(msg, node);
                }

                if (Thread.interrupted())
                {
                    LOG.info("Stopping...");
                    stop();
                }
            } catch (Exception e) {
                // bwahaha
                LOG.error("SXMT ERROR:\n", e);
            }
		}

//		if (hosebirdClient.isDone()) {
//			System.out.println("Client connection closed unexpectedly");
//		}
	}

	public void stop()
	{
		hosebirdClient.stop();
	}

	private void buildAndStoreTweet(String msg, JsonNode node)
	{
		// Build tweet and store it
		Long tweetId = node.get("id").asLong();
		String userName = getUserName(node);

		String tweetText = getTweetText(node);
		Long userID = getUserID(node);
		SXMDialect dialect = userIDs.get(userID);
		String songName = dialect.getSongTitle(tweetText);
		String artist = dialect.getArtist(tweetText);

		DateTime origination = getOriginationData(node);
		Tweet tweet =  new Tweet(tweetId, songName, artist, userName, userName, tweetText, userID, origination, msg);
		System.out.println("Tweet: " + tweet.toString());
		try {
			TweetStorer.storeTweet(tweet);
		} catch (SQLException e) {
			LOG.error("SQLException: {}", e.getMessage());
			LOG.error("SQLState: {}", e.getSQLState());
			LOG.error("VendorError: {}", e.getErrorCode());
			LOG.error("Exception encountered while storing tweet!", e);
		} catch (Exception e)
		{
			LOG.error("Exception encountered while storing tweet!", e);
		}
	}

	public void addUser(Long id, SXMDialect dialect)
	{
		userIDs.put(id, dialect);
	}

	public void removeUser(Long id)
	{
		if (userIDs.containsKey(id))
		{
			userIDs.remove(id);
		}
	}

	private String getTweetText(JsonNode node)
	{
		return node.get("text").getTextValue();
	}

	private Long getUserID(JsonNode node)
	{
		return node.get("user").get("id").asLong();
	}

	private String getUserName(JsonNode node)
	{
		return node.get("user").get("screen_name").getTextValue();
	}

	private DateTime getOriginationData(JsonNode node)
	{
		String originationStr = node.get("created_at").getTextValue();
		DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy");
		return formatter.parseDateTime(originationStr);
	}
}
