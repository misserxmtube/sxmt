package com.sxmt.twitter;

import com.google.common.collect.Lists;

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
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	private final Logger log = LoggerFactory.getLogger(UserStreamReader.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	private final String consumerKey = "dQskdemPDk9E6AjgmpcIfMpqi";
	private final String consumerSecret = "TUkTc5aMsbZTPc8ndQ5UQDkWocYx5wR9ZSKHoJIEEdpZAQpuR4";
	private final String token = "3089407595-97kWtUYxO7WtL1oUL54xSdHGdY2G6Kt3YIIpe3Y";
	private final String secret = "JXUhfSdQII6qkv2bFSmR8FWyEhBvCSPHb0DgN7CPIvQDz";

	private Client hosebirdClient;
	private BlockingQueue<String> msgQueue;
	private BlockingQueue<Event> eventQueue;
	private List<Long> userIDs;

	public UserStreamReader()
	{
		userIDs = new ArrayList<Long>();

		/** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
		msgQueue = new LinkedBlockingQueue<String>(100000);
		eventQueue = new LinkedBlockingQueue<Event>(1000);
	}

	@Override
	public void run()
	{
		/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint().followings(userIDs);
		hosebirdEndpoint.stallWarnings(false);

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
			String msg = "";
			try {
				msg = msgQueue.poll(10, TimeUnit.SECONDS);
			} catch (Exception e)
			{
				// interrupted while polling
//				log.error("Unexpected error while polling", e);
				stop();
			}

			if (msg == null || msg.equals(""))
			{
				System.out.println("Did not receive a message in 10 seconds");
			} else
			{
//				System.out.println(msg);
				JsonNode node;
				try {
					node = objectMapper.readTree(msg);
				} catch (Exception e)
				{
					log.error("Exception while parsing response", e);
					throw new RuntimeException(e);
				}

				buildAndStoreTweet(msg, node);
			}

			if (Thread.interrupted())
			{
				stop();
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
		Long id = node.get("id").asLong();
		String songName = getSongName(node);
		String artist = getArtist(node);
		String userName = getUserName(node);
		String tweetText = getTweetText(node);
		Long userID = getUserID(node);
		Date origination = getOriginationData(node);
		Tweet tweet =  new Tweet(id, songName, artist, userName, userName, tweetText, userID, origination, msg);
		System.out.println("Tweet: " + tweet.toString());
		try {
			TweetStorer.storeTweet(tweet);
		} catch (Exception e)
		{
			log.error("Exception while storing tweet", e);
		}
	}

	public void addUser(Long id)
	{
		userIDs.add(id);
	}

	public void removeUser(Long id)
	{
		int x = userIDs.indexOf(id);
		if (x != -1)
		{
			userIDs.remove(x);
		}
	}

	private String getTweetText(JsonNode node)
	{
		return node.get("text").getTextValue();
	}

	// BPM: Tiesto/Allure - Pair Of Dice playing on #BPM -
	private String getSongName(JsonNode node)
	{
		String text = node.get("text").getTextValue();
		int poIndex = text.indexOf("playing on");
		int dIndex = text.indexOf('-');

		return text.substring(dIndex+1, poIndex).trim();
	}

	private String getArtist(JsonNode node)
	{
		String text = node.get("text").getTextValue();
		int dIndex = text.indexOf('-');

		return text.substring(0, dIndex).trim();
	}

	private Long getUserID(JsonNode node)
	{
		return node.get("user").get("id").asLong();
	}

	private String getUserName(JsonNode node)
	{
		return node.get("user").get("screen_name").getTextValue();
	}

	private Date getOriginationData(JsonNode node)
	{
		String originationStr = node.get("created_at").getTextValue();
		DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
		try {
			return format.parse(originationStr);
		} catch (Exception e)
		{
			log.error("Exception when parsing date format", e);
			return new Date(0);
		}
	}
}
