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
	private final Long referenceTweetId;

	public VideoForDisplay(String songName, String artist, String videoTitle, String description, String videoId, String channelName, Long relevantTweetId)
	{
		this(songName, artist, videoTitle, description, videoId, channelName, relevantTweetId, null);
	}

	public VideoForDisplay(String songName, String artist, String videoTitle, String description, String videoId, String channelName, Long relevantTweetId, Long referenceTweetId)
	{
		this.songName = songName;
		this.artist = artist;
		this.videoTitle = videoTitle;
		this.description = description;
		this.videoId = videoId;
		this.channelName = channelName;
		this.relevantTweetId = relevantTweetId;
		this.referenceTweetId = referenceTweetId;
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

	public Long getReferenceTweetId()
	{
		return referenceTweetId;
	}
}
