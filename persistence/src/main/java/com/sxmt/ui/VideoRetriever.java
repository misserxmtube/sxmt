package com.sxmt.ui;

import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VideoRetriever
{
	public static String getNewestVideo() throws SQLException
	{
        String vidId = "dQw4w9WgXcQ";
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final Statement statement = connection.createStatement())
        {
            //TODO filter by channel
            ResultSet results = statement.executeQuery(
                    "SELECT vids.videoId FROM " + TableNames.VIDEOS + " AS vids " +
                    "INNER JOIN " + TableNames.TWEETS + " AS twits " +
                        "ON twits.tweetId = vids.tweetId " +
                    "ORDER BY twits.origination DESC " +
                    "LIMIT 1"
            );

            while(results.next()){
                vidId = results.getString(1);
            }
        }
		return vidId;
	}

	public static String getNextVideo(Long previousId) throws SQLException
	{
        String vidId = "dQw4w9WgXcQ";
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final Statement statement = connection.createStatement())
        {
            //TODO filter by channel
            ResultSet results = statement.executeQuery(
                    "SELECT vids.videoId FROM " + TableNames.VIDEOS + " AS vids " +
                    "INNER JOIN " + TableNames.TWEETS + " AS twits " +
                    "ON twits.tweetId = vids.tweetId " +
                    "WHERE twits.origination > ( " +
                        "SELECT origination FROM " + TableNames.TWEETS +
                        "WHERE tweetId = " + previousId + " )" +
                    "ORDER BY twits.origination ASC " +
                    "LIMIT 1"
            );

            while(results.next()){
                vidId = results.getString(1);
            }
        }
        return vidId;
	}
}
