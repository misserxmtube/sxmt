package com.sxmt.twitter;

import com.sxmt.YoutubeFetcher;
import com.sxmt.YoutubeRecord;
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
				" (" + TweetsFields.TWEET_ID + ", " + TweetsFields.VIDEO_ID + ", " + TweetsFields.STATION_ID + ", " + TweetsFields.TWEET_TEXT + ", " + TweetsFields.SONG_NAME + ", " + TweetsFields.ARTIST + ", " + TweetsFields.ORIGINATION + ", " + TweetsFields.JSON_BLOB + ") VALUES(?,?,?,?,?,?,?,?)";
		final YoutubeRecord youtubeRecord = YoutubeFetcher.getYoutubeRecord(tweet.getSongName(), tweet.getArtist());
		VideoStorer.storeVideo(youtubeRecord);

		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement stationStatement = connection.prepareStatement(stationInsert);
				final PreparedStatement tweetStatement = connection.prepareStatement(tweetInsert))
		{
			stationStatement.setLong(1, tweet.getStationId());
			stationStatement.setString(2, tweet.getStationName());
			stationStatement.setString(3, tweet.getStationHandle());
			stationStatement.execute();

			tweetStatement.setLong(1, tweet.getId());
			tweetStatement.setString(2, youtubeRecord.getVideoId());
			tweetStatement.setLong(3, tweet.getStationId());
			tweetStatement.setString(4, tweet.getTweetText());
			tweetStatement.setString(5, tweet.getSongName());
			tweetStatement.setString(6, tweet.getArtist());
			tweetStatement.setString(7, format.format(tweet.getOrigination().toDate()));
			tweetStatement.setString(8, tweet.getFullTweet());
			tweetStatement.execute();
		}

		//this better be the newest video...
		//TODO update materialized view to have this be the station's newest video
//		VideoRetriever.setNewestVideo(tweet.getStationId(), new VideoForDisplay(tweet.getSongName(), tweet.getArtist(), youtubeRecord.getTitle(), youtubeRecord.getVideoId(), youtubeRecord.getChannelTitle(), youtubeRecord.getThumbnail(), tweet.getId(), null));
	}
}
