package com.sxmt.ui;

/**
 * Class FillerVideo
 *
 * @since 05/09/2015
 */
public class FillerVideo
{
	private final String songName;
	private final String artist;
	private final String videoTitle;
	private final String videoId;
	private final String channelName;
	private final String thumbnail;
	private final Long relevantTweetId;

	public FillerVideo(String songName, String artist, String videoTitle, String videoId, String channelName, String thumbnail, Long relevantTweetId)
	{
		this.songName = songName;
		this.artist = artist;
		this.videoTitle = videoTitle;
		this.videoId = videoId;
		this.channelName = channelName;
		this.thumbnail = thumbnail;
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
				'}';
	}
}
