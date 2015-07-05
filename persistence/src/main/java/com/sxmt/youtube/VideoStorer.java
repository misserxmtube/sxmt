package com.sxmt.youtube;

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
	public static void storeVideo(YoutubeRecord youtubeRecord) throws SQLException
	{
		//insert the 'youtubeRecord'
		final String query = "INSERT IGNORE INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.VIDEOS +
				" (" + VideosFields.VIDEO_ID + ", " + VideosFields.VIDEO_TITLE + ", " + VideosFields.CHANNEL_NAME + ", " + VideosFields.VIDEO_THUMBNAIL + ", " + VideosFields.VIDEO_TYPE + ") VALUES(?,?,?,?,?)";
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement statement = connection.prepareStatement(query))
		{
			statement.setString(1, youtubeRecord.getVideoId());
			statement.setString(2, youtubeRecord.getTitle());
            statement.setString(3, youtubeRecord.getChannelTitle());
            statement.setString(4, youtubeRecord.getThumbnail());
			statement.setString(5, youtubeRecord.getVideoType().getTypeName());
			statement.execute();
		}
	}
}
