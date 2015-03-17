package com.sxmt.twitter;

import com.sxmt.YoutubeFetcher;
import com.sxmt.config.Properties;
import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;
import com.sxmt.youtube.VideoStorer;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class TweetStorer
{
	public static void storeTweet(Tweet tweet) throws SQLException
	{
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final Statement statement = connection.createStatement())
		{
			//insert user if not exists
			statement.execute("INSERT IGNORE INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.USERS + " (userId, userName, userHandle) VALUES (" + tweet.getUserId() + ",\"" + StringEscapeUtils.escapeJava(tweet.getUserName()) + "\",\"" + StringEscapeUtils.escapeJava(tweet.getUserHandle()) + "\")");

			//insert the 'tweet'
			final String query = "INSERT INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.TWEETS + " (tweetId, userId, twitterText, songName, artist, origination, jsonBlob) VALUES (" +
					tweet.getId() + "," + tweet.getUserId() + ",\"" + StringEscapeUtils.escapeJava(tweet.getTweetText()) + "\",\"" + StringEscapeUtils.escapeJava(tweet.getSongName()) + "\",\"" + StringEscapeUtils.escapeJava(tweet.getArtist()) + "\",\"" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tweet.getOrigination().toDate()) + "\",\"" + StringEscapeUtils.escapeJava(tweet.getBlob()) + "\")";
			statement.execute(query);
		}

		VideoStorer.storeVideo(YoutubeFetcher.getYoutubeRecord(tweet.getSongName(), tweet.getArtist()), tweet.getId());
	}
}
