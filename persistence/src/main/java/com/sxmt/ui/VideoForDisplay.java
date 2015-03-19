package com.sxmt.ui;

import org.apache.commons.lang3.StringEscapeUtils;

public class VideoForDisplay
{
	private final String songName;
	private final String artist;
	private final String videoTitle;
	private final String videoId;
	private final String channelName;
	private final String thumbnail;
	private final Long relevantTweetId;
	private final Long referenceTweetId;

	public VideoForDisplay(String songName, String artist, String videoTitle, String videoId, String channelName, String thumbnail, Long relevantTweetId)
	{
		this(songName, artist, videoTitle, videoId, channelName, thumbnail, relevantTweetId, null);
	}

	public VideoForDisplay(String songName, String artist, String videoTitle, String videoId, String channelName, String thumbnail, Long relevantTweetId, Long referenceTweetId)
	{
		this.songName = StringEscapeUtils.unescapeHtml4(songName);
		this.artist = StringEscapeUtils.unescapeHtml4(artist);
		this.videoTitle = StringEscapeUtils.unescapeHtml4(videoTitle);
		this.videoId = StringEscapeUtils.unescapeHtml4(videoId);
		this.channelName = StringEscapeUtils.unescapeHtml4(channelName);
		this.thumbnail = StringEscapeUtils.unescapeHtml4(thumbnail);
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

	public String getVideoId()
	{
		return videoId;
	}

	public String getChannelName()
	{
		return channelName;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public Long getRelevantTweetId()
	{
		return relevantTweetId;
	}

	public Long getReferenceTweetId()
	{
		return referenceTweetId;
	}

	@Override
	public String toString() {
		return "VideoForDisplay{" +
				"songName='" + songName + '\'' +
				", artist='" + artist + '\'' +
				", videoTitle='" + videoTitle + '\'' +
				", videoId='" + videoId + '\'' +
				", channelName='" + channelName + '\'' +
				", thumbnail='" + thumbnail + '\'' +
				", relevantTweetId=" + relevantTweetId +
				", referenceTweetId=" + referenceTweetId +
				'}';
	}
}
