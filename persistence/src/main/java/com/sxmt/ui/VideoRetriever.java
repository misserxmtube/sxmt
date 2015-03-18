package com.sxmt.ui;

import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VideoRetriever
{
	Logger LOG = LoggerFactory.getLogger(VideoRetriever.class);

	public static VideoForDisplay getNewestVideo() throws SQLException
	{
		Log.info("Getting newest video");
		VideoForDisplay videoForDisplay = null;
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final Statement statement = connection.createStatement())
        {
            //TODO filter by channel
            ResultSet results = statement.executeQuery(
                    " SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId FROM " + TableNames.VIDEOS + " AS vids " +
                    " INNER JOIN " + TableNames.TWEETS + " AS twits " +
                        " ON twits.tweetId = vids.tweetId " +
                    " ORDER BY twits.origination DESC " +
                    " LIMIT 1"
            );

            while(results.next()){

	            videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), "", results.getString(1), results.getString(5), null/*THUMBNAIL*/, results.getLong(6));
            }
        }
		//TODO do something instead of returning null when there is nothing found
		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(1L);
		}
		Log.info("Retrieved video: {}", videoForDisplay);
		return videoForDisplay;
	}

	public static VideoForDisplay getVideo(Long id) throws SQLException {
		return getVideo(id, true);
	}

	public static VideoForDisplay getVideo(Long tweet, boolean getNext) throws SQLException
	{
		Log.info("Getting " + (getNext ? "next " : "") + "video for {}", tweet);
		VideoForDisplay videoForDisplay = null;
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final Statement statement = connection.createStatement())
        {
            //TODO filter by channel
            ResultSet results = statement.executeQuery(
                    " SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId FROM " + TableNames.VIDEOS + " AS vids " +
                    " INNER JOIN " + TableNames.TWEETS + " AS twits " +
                     "ON twits.tweetId = vids.tweetId " +
                    " WHERE twits.origination " + (getNext ? ">" : "=") + " ( " +
                        " SELECT origination FROM " + TableNames.TWEETS +
                        " WHERE tweetId = " + tweet + " ) " +
		                    //TODO split up the calls and do orderby origination ASC?
                    " LIMIT 1"
            );

            if(results.next()){
	            videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), "", results.getString(1), results.getString(5), null/*THUMBNAIL*/, results.getLong(6));
            }
        }

		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(tweet);
		}
		Log.info("Retrieved video", videoForDisplay);
        return videoForDisplay;
	}

	private static VideoForDisplay getFillerVideo(Long tweet) throws SQLException
	{
		Log.info("Getting filler video for {}", tweet);
		VideoForDisplay videoForDisplay = null;
		final String fillerSql = "SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId " +
				" FROM " + TableNames.VIDEOS + " AS vids\n" +
				" INNER JOIN " + TableNames.TWEETS + " AS twits\n" +
				" ON vids.tweetId = twits.tweetId\n" +
				" INNER JOIN " + TableNames.USERS + " AS u\n" +
				" ON twits.userId = u.userId\n" +
				" WHERE u.userName = 'bpm_playlist'\n" +
				" AND DATE_SUB(NOW(), INTERVAL 2 HOUR) > twits.origination\n" +
				" AND twits.tweetId != " + tweet +
				" ORDER BY RAND()\n" +
				" LIMIT 1";
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final Statement statement = connection.createStatement())
		{
			//TODO filter by channel
			ResultSet results = statement.executeQuery(fillerSql);

			if(results.next()){
				videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), "", results.getString(1), results.getString(5), null/*THUMBNAIL*/, results.getLong(6), tweet);
			}
		}

		if(videoForDisplay == null)
		{
			videoForDisplay = new VideoForDisplay("Never Gonna Give You Up", "Rick Astley", "Rick Astley - Never Gonna Give You Up", "", "dQw4w9WgXcQ", "Troll", null/*THUMBNAIL*/, null, tweet); // TODO temporarily adding null for tweet on these ones (easier for ui but should remove eventually)
		}
		return videoForDisplay;
	}
}
