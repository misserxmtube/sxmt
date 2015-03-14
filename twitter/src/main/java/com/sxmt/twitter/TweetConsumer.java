package com.sxmt.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Consumes tweets using a UserStreamReader. In order to add/remove tweeters, use stopConsuming() beforehand and
 * resume with startConsuming().
 *
 * Created by remo.fischione on 3/14/2015.
 */
public class TweetConsumer
{
	private Logger log = LoggerFactory.getLogger(TweetConsumer.class);

	private final String consumerKey = "dQskdemPDk9E6AjgmpcIfMpqi";
	private final String consumerSecret = "TUkTc5aMsbZTPc8ndQ5UQDkWocYx5wR9ZSKHoJIEEdpZAQpuR4";
	private final String token = "3089407595-97kWtUYxO7WtL1oUL54xSdHGdY2G6Kt3YIIpe3Y";
	private final String secret = "JXUhfSdQII6qkv2bFSmR8FWyEhBvCSPHb0DgN7CPIvQDz";

	private UserStreamReader reader;
	private Thread runner;
	private Twitter twitter;

	public TweetConsumer()
	{
		reader = new UserStreamReader();
		runner = new Thread(reader);
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(token)
				.setOAuthAccessTokenSecret(secret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	public void addUser(long userID)
	{
		reader.addUser(userID);
	}

	public void addUser(String user)
	{
		try {
			User tUser = twitter.users().showUser(user);
			reader.addUser(tUser.getId());
		} catch (Exception e)
		{
			log.error("addUser(String)", e);
		}
	}

	public void startConsuming()
	{
		runner.start();
	}

	public void stopConsuming()
	{
		runner.interrupt();
	}
}
