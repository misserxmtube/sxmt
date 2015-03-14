package com.sxmt.youtube;

import com.sxmt.YoutubeRecord;
import com.sxmt.config.Properties;
import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class VideoStorer
{
	public static void storeVideo(YoutubeRecord youtubeRecord, Long tweetId) throws SQLException
	{
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final Statement statement = connection.createStatement())
		{
			//insert the 'youtubeRecord'
			final String query = "INSERT INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.VIDEOS + " (tweetId, videoTitle, videoId, channelName) VALUES (" +
					tweetId + ",'" + youtubeRecord.getTitle() + "','" + youtubeRecord.getVideoId() + "','" + youtubeRecord.getChannelTitle() + "')";
			statement.execute(query);
		}
	}
}
