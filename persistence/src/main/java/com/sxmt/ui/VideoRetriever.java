package com.sxmt.ui;

import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VideoRetriever
{
	public static VideoForDisplay getNewestVideo() throws SQLException
	{
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

	            videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), "", results.getString(1), results.getString(5), results.getLong(6));
            }
        }
		//TODO do something instead of returning null when there is nothing found
		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(1L);
		}

		return videoForDisplay;
	}

	public static VideoForDisplay getNextVideo(Long previousId) throws SQLException
	{
		VideoForDisplay videoForDisplay = null;
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final Statement statement = connection.createStatement())
        {
            //TODO filter by channel
            ResultSet results = statement.executeQuery(
                    " SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId FROM " + TableNames.VIDEOS + " AS vids " +
                    " INNER JOIN " + TableNames.TWEETS + " AS twits " +
                     "ON twits.tweetId = vids.tweetId " +
                    " WHERE twits.origination > ( " +
                        " SELECT origination FROM " + TableNames.TWEETS +
                        " WHERE tweetId = " + previousId + " ) " +
                    " ORDER BY twits.origination ASC " +
                    " LIMIT 1"
            );

            while(results.next()){
	            videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), "", results.getString(1), results.getString(5), results.getLong(6));
            }
        }
		//TODO do something instead of returning null when there is nothing found
		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(previousId);
		}

        return videoForDisplay;
	}

	private static VideoForDisplay getFillerVideo(Long previousTweet)
	{
		return new VideoForDisplay("Never Gonna Give You Up", "Rick Astley", "Rick Astley - Never Gonna Give You Up", "", "dQw4w9WgXcQ", "Troll", previousTweet);
	}
}
