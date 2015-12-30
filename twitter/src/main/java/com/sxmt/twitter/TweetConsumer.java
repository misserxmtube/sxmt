package com.sxmt.twitter;

import com.sxmt.twitter.dialects.SXMDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Properties;

/**
 * Consumes tweets using a UserStreamReader. In order to add/remove tweeters, use stopConsuming() beforehand and
 * resume with startConsuming().
 *
 * Created by remo.fischione on 3/14/2015.
 */
public class TweetConsumer
{
	private Logger log = LoggerFactory.getLogger(TweetConsumer.class);

	private UserStreamReader reader;
	private Thread runner;
	private Twitter twitter;

	public TweetConsumer(Properties properties)
	{
		reader = new UserStreamReader();
		runner = new Thread(reader);
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(properties.getProperty("twitter.consumerKey"))
				.setOAuthConsumerSecret(properties.getProperty("twitter.consumerSecret"))
				.setOAuthAccessToken(properties.getProperty("twitter.token"))
				.setOAuthAccessTokenSecret(properties.getProperty("twitter.secret"));
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}

	public void addUser(long userID, SXMDialect dialect)
	{
		reader.addUser(userID, dialect);
	}

	public void addUser(String user, SXMDialect dialect)
	{
		try {
			User tUser = twitter.users().showUser(user);
			reader.addUser(tUser.getId(), dialect);
		} catch (Exception e)
		{
			log.error("addUser(String, SXMDialect)", e);
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
