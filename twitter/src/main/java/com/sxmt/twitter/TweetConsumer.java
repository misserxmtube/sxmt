package com.sxmt.twitter;

/**
 * Consumes tweets using a UserStreamReader
 *
 * Created by remo.fischione on 3/14/2015.
 */
public class TweetConsumer
{
	private UserStreamReader reader;
	private Thread runner;

	public TweetConsumer()
	{
		reader = new UserStreamReader();
		runner = new Thread(reader);
	}

	public void addUser(long userID)
	{
		reader.addUser(userID);
	}

//	public void addUser(String user)
//	{
//
//	}

	public void startConsuming()
	{
		runner.start();
	}

	public void stopConsuming()
	{
		runner.interrupt();
	}
}
