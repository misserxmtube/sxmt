package com.sxmt.twitter;

import com.sxmt.YoutubeFetcher;
import com.sxmt.config.Properties;
import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;
import com.sxmt.youtube.VideoStorer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

public class TweetStorer
{
//	private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void storeTweet(Tweet tweet) throws SQLException, ParseException
	{
		//insert user if not exists
		final String stationInsert = "INSERT IGNORE INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.STATIONS +
				" (stationId, stationName, stationHandle) VALUES(?,?,?)";
		//insert the 'tweet'
		final String tweetInsert = "INSERT INTO " + Properties.getInstance().getAppDatabaseName() + "." + TableNames.TWEETS +
				" (tweetId, stationId, twitterText, songName, artist, origination, jsonBlob) VALUES(?,?,?,?,?,?,?)";
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement stationStatement = connection.prepareStatement(stationInsert);
				final PreparedStatement tweetStatement = connection.prepareStatement(tweetInsert))
		{
			//TODO index variables
			stationStatement.setLong(1, tweet.getStationId());
			stationStatement.setString(2, tweet.getStationName());
			stationStatement.setString(3, tweet.getStationHandle());
			stationStatement.execute();

			//TODO index variables
			tweetStatement.setLong(1, tweet.getId());
			tweetStatement.setLong(2, tweet.getStationId());
			tweetStatement.setString(3, tweet.getTweetText());
			tweetStatement.setString(4, tweet.getSongName());
			tweetStatement.setString(5, tweet.getArtist());
			tweetStatement.setDate(6, new Date(tweet.getOrigination().getMillis()));
//			tweetStatement.setDate(6, new Date(format.parse(tweet.getOrigination().toString()).getTime()));
			tweetStatement.setString(7, tweet.getFullTweet());
			tweetStatement.execute();
		}

		VideoStorer.storeVideo(YoutubeFetcher.getYoutubeRecord(tweet.getSongName(), tweet.getArtist()), tweet.getId());
	}
}
