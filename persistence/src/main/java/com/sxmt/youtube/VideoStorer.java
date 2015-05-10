package com.sxmt.youtube;

import com.sxmt.VideoType;
import com.sxmt.YoutubeRecord;
import com.sxmt.config.Properties;
import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;
import com.sxmt.connection.VideosFields;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VideoStorer
{
	public static void storeVideo(YoutubeRecord youtubeRecord, Long tweetId) throws SQLException
	{
		//insert the 'youtubeRecord'
		final String query = "INSERT INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.VIDEOS +
				" (" + VideosFields.TWEET_ID + ", " + VideosFields.VIDEO_TITLE + ", " + VideosFields.VIDEO_ID + ", " + VideosFields.CHANNEL_NAME + ", " + VideosFields.VIDEO_THUMBNAIL + ", " + VideosFields.VIDEO_TYPE + ") VALUES(?,?,?,?,?,?)";
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement statement = connection.prepareStatement(query))
		{
			statement.setLong(1, tweetId);
			statement.setString(2, youtubeRecord.getTitle());
			statement.setString(3, youtubeRecord.getVideoId());
            statement.setString(4, youtubeRecord.getChannelTitle());
            statement.setString(5, youtubeRecord.getThumbnail());
			statement.setString(6, youtubeRecord.getVideoType().getTypeName());
			statement.execute();
		}
	}
}
