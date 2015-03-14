package com.sxmt.twitter;

import java.util.Date;

public class Tweet
{
	private final String id;
	private final String songName;
	private final String artist;
	private final String userName;
	private final String userId;
	private final Date origination;
	private final String blob;

	public Tweet(String id, String songName, String artist, String userName, String userId, Date origination, String blob)
	{
		this.id = id;
		this.songName = songName;
		this.artist = artist;
		this.userName = userName;
		this.userId = userId;
		this.origination = origination;
		this.blob = blob;
	}

	public String getId()
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

	public String getUserId()
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
}
