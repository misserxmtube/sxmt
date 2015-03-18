package com.sxmt.youtube;

import com.sxmt.YoutubeRecord;
import com.sxmt.config.Properties;
import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;
import org.apache.commons.lang3.StringEscapeUtils;

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
			final String query = "INSERT INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.VIDEOS + " (tweetId, videoTitle, videoId, channelName, videoThumbnail) VALUES (" +
					tweetId + ",\"" + StringEscapeUtils.escapeJava(youtubeRecord.getTitle()) + "\",\"" + StringEscapeUtils.escapeJava(youtubeRecord.getVideoId()) + "\",\"" + StringEscapeUtils.escapeJava(youtubeRecord.getChannelTitle()) + "\",\"" + StringEscapeUtils.escapeJava(youtubeRecord.getThumbnail()) + "\")";
			statement.execute(query);
		}
	}
}
