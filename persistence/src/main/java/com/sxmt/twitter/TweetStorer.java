package com.sxmt.twitter;

import com.sxmt.YoutubeFetcher;
import com.sxmt.config.Properties;
import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.StationsFields;
import com.sxmt.connection.TableNames;
import com.sxmt.connection.TweetsFields;
import com.sxmt.youtube.VideoStorer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TweetStorer
{
	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void storeTweet(Tweet tweet) throws SQLException, ParseException
	{
		//insert user if not exists
		final String stationInsert = "INSERT IGNORE INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.STATIONS +
				" (" + StationsFields.STATION_ID + ", " + StationsFields.STATION_NAME + ", " + StationsFields.STATION_HANDLE + ") VALUES(?,?,?)";
		//insert the 'tweet'
		final String tweetInsert = "INSERT INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.TWEETS +
				" (" + TweetsFields.TWEET_ID + ", " + TweetsFields.STATION_ID + ", " + TweetsFields.TWEET_TEXT + ", " + TweetsFields.SONG_NAME + ", " + TweetsFields.ARTIST + ", " + TweetsFields.ORIGINATION + ", " + TweetsFields.JSON_BLOB + ") VALUES(?,?,?,?,?,?,?)";
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement stationStatement = connection.prepareStatement(stationInsert);
				final PreparedStatement tweetStatement = connection.prepareStatement(tweetInsert))
		{
			stationStatement.setLong(1, tweet.getStationId());
			stationStatement.setString(2, tweet.getStationName());
			stationStatement.setString(3, tweet.getStationHandle());
			stationStatement.execute();

			tweetStatement.setLong(1, tweet.getId());
			tweetStatement.setLong(2, tweet.getStationId());
			tweetStatement.setString(3, tweet.getTweetText());
			tweetStatement.setString(4, tweet.getSongName());
			tweetStatement.setString(5, tweet.getArtist());
			tweetStatement.setString(6, format.format(tweet.getOrigination().toDate()));
			tweetStatement.setString(7, tweet.getFullTweet());
			tweetStatement.execute();
		}

		VideoStorer.storeVideo(YoutubeFetcher.getYoutubeRecord(tweet.getSongName(), tweet.getArtist()), tweet.getId());
	}
}
