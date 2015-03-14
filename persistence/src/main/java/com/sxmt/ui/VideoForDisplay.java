package com.sxmt.ui;

public class VideoForDisplay
{
	private final String songName;
	private final String artist;
	private final String videoTitle;
	private final String description;
	private final String videoId;
	private final String channelName;
	private final Long relevantTweetId;

	public VideoForDisplay(String songName, String artist, String videoTitle, String description, String videoId, String channelName, Long relevantTweetId)
	{
		this.songName = songName;
		this.artist = artist;
		this.videoTitle = videoTitle;
		this.description = description;
		this.videoId = videoId;
		this.channelName = channelName;
		this.relevantTweetId = relevantTweetId;
	}

	public String getSongName()
	{
		return songName;
	}

	public String getArtist()
	{
		return artist;
	}

	public String getVideoTitle()
	{
		return videoTitle;
	}

	public String getDescription()
	{
		return description;
	}

	public String getVideoId()
	{
		return videoId;
	}

	public String getChannelName()
	{
		return channelName;
	}

	public Long getRelevantTweetId()
	{
		return relevantTweetId;
	}
}
