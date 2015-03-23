package com.sxmt.youtube;

import com.sxmt.VideoType;
import com.sxmt.YoutubeRecord;
import com.sxmt.config.Properties;
import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VideoStorer
{
	public static void storeVideo(YoutubeRecord youtubeRecord, Long tweetId) throws SQLException
	{
		//insert the 'youtubeRecord'
		final String query = "INSERT INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.VIDEOS +
				" (tweetId, videoTitle, videoId, channelName, videoThumbnail, videoType) VALUES(?,?,?,?,?,?)";
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement statement = connection.prepareStatement(query))
		{
			//TODO use index variables
			statement.setLong(1, tweetId);
			statement.setString(2, youtubeRecord.getTitle());
			statement.setString(3, youtubeRecord.getVideoId());
            statement.setString(4, youtubeRecord.getChannelTitle());
            statement.setString(5, youtubeRecord.getThumbnail());
			statement.setString(6, VideoType.NORMAL.name());
			//TODO switch to non-hardcoded one
//			statement.setString(6, youtubeRecord.getVideoType().name());
			statement.execute();
		}
	}
}
