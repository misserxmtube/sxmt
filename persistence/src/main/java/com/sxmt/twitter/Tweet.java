package com.sxmt.twitter;

import org.joda.time.DateTime;

public class Tweet
{
	private final Long id;
	private final String songName;
	private final String artist;
	private final String stationName;
	private final String stationHandle;
	private final String tweetText;
	private final Long stationId;
	private final DateTime origination;
	private final String fullTweet;

	public Tweet(Long id, String songName, String artist, String stationName, String stationHandle, String tweetText, Long stationId, DateTime origination, String fullTweet)
	{
		this.id = id;
		this.songName = songName;
		this.artist = artist;
		this.stationName = stationName;
		this.stationHandle = stationHandle;
		this.tweetText = tweetText;
		this.stationId = stationId;
		this.origination = origination;
		this.fullTweet = fullTweet;
	}

	public Long getId()
	{
		return id;
	}

	public String getSongName()
	{
		return songName;
	}

	public String getArtist()
	{
		return artist;
	}

	public String getStationName()
	{
		return stationName;
	}

	public String getTweetText()
	{
		return tweetText;
	}

	public Long getStationId()
	{
		return stationId;
	}

	public DateTime getOrigination()
	{
		return origination;
	}

	public String getFullTweet()
	{
		return fullTweet;
	}

	public String getStationHandle()
	{
		return stationHandle;
	}

	@Override
	public String toString()
	{
		return "Tweet{" +
				"id='" + id + '\'' +
				", songName='" + songName + '\'' +
				", artist='" + artist + '\'' +
				", stationName='" + stationName + '\'' +
				", stationHandle='" + stationHandle + '\'' +
				", tweetText='" + tweetText + '\'' +
				", stationId='" + stationId + '\'' +
				", origination=" + origination +
				", fullTweet='" + fullTweet + '\'' +
				'}';
	}
}
