package com.sxmt.twitter;

import java.util.Date;

public class Tweet
{
	private final Long id;
	private final String songName;
	private final String artist;
	private final String userName;
	private final String userHandle;
	private final String tweetText;
	private final Long userId;
	private final Date origination;
	private final String blob;

	public Tweet(Long id, String songName, String artist, String userName, String userHandle, String tweetText, Long userId, Date origination, String blob)
	{
		this.id = id;
		this.songName = songName;
		this.artist = artist;
		this.userName = userName;
		this.userHandle = userHandle;
		this.tweetText = tweetText;
		this.userId = userId;
		this.origination = origination;
		this.blob = blob;
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

	public String getUserName()
	{
		return userName;
	}

	public String getTweetText()
	{
		return tweetText;
	}

	public Long getUserId()
	{
		return userId;
	}

	public Date getOrigination()
	{
		return origination;
	}

	public String getBlob()
	{
		return blob;
	}

	public String getUserHandle()
	{
		return userHandle;
	}

	@Override
	public String toString()
	{
		return "Tweet{" +
				"id='" + id + '\'' +
				", songName='" + songName + '\'' +
				", artist='" + artist + '\'' +
				", userName='" + userName + '\'' +
				", userHandle='" + userHandle + '\'' +
				", userId='" + userId + '\'' +
				", origination=" + origination +
				", blob='" + blob + '\'' +
				'}';
	}
}
