package com.sxmt.twitter;

import com.sxmt.twitter.dialects.BPMDialect;

/**
 *
 * Created by remo.fischione on 3/14/2015.
 */
public class RunTweetConsumer
{
	public static void main(String[] args) throws Exception
	{
		TweetConsumer consumer = new TweetConsumer();
		consumer.addUser("bpm_playlist", new BPMDialect());
		consumer.startConsuming();

//		Thread.sleep(1000 * 20);
//
//		consumer.stopConsuming();
	}
}