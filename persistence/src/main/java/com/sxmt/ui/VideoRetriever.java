package com.sxmt.ui;

import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VideoRetriever
{
	private static final Logger LOG = LoggerFactory.getLogger(VideoRetriever.class);

	public static VideoForDisplay getNewestVideo(Long stationId) throws SQLException
	{
		LOG.info("Getting newest video");
		VideoForDisplay videoForDisplay = null;
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final Statement statement = connection.createStatement())
        {
	        //TODO use prepared statement
            ResultSet results = statement.executeQuery(
                    " SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId, vids.videoThumbnail FROM " + TableNames.VIDEOS + " AS vids " +
                    " INNER JOIN " + TableNames.TWEETS + " AS twits " +
                    " ON twits.tweetId = vids.tweetId " +
                    " AND twits.stationId = " + stationId +
                    " ORDER BY twits.origination DESC " +
                    " LIMIT 1"
            );

            while(results.next()){
	            //TODO use column var
	            videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), results.getString(1), results.getString(5), results.getString(7), results.getLong(6));
            }
        }

		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(1L, stationId);
		}
		LOG.info("Retrieved video: {}", videoForDisplay);
		return videoForDisplay;
	}

	public static VideoForDisplay getVideo(Long station, Long tweet) throws SQLException
	{
		LOG.info("Getting video for {}", tweet);
		VideoForDisplay videoForDisplay = null;
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final Statement statement = connection.createStatement())
        {
	        //TODO use prepared statement
            ResultSet results = statement.executeQuery(
                    " SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId, vids.videoThumbnail FROM " + TableNames.VIDEOS + " AS vids " +
                    " INNER JOIN " + TableNames.TWEETS + " AS twits " +
                    " ON twits.tweetId = vids.tweetId " +
                    " WHERE twits.tweetId = " + tweet +
                    " AND twits.stationId = " + station
            );

            if(results.next()){
	            //TODO use column var
	            videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), results.getString(1), results.getString(5), results.getString(7), results.getLong(6));
            }
        }

		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(tweet, station);
		}
		LOG.info("Retrieved video", videoForDisplay);
        return videoForDisplay;
	}

	public static VideoForDisplay getNextVideo(Long station, Long tweet) throws SQLException
	{
		LOG.info("Getting next video for {}", tweet);
		VideoForDisplay videoForDisplay = null;
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final Statement statement = connection.createStatement())
		{
			//TODO use prepared statement
			ResultSet results = statement.executeQuery(
					" SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId, vids.videoThumbnail FROM " + TableNames.VIDEOS + " AS vids " +
							" INNER JOIN " + TableNames.TWEETS + " AS twits " +
							"ON twits.tweetId = vids.tweetId " +
							" WHERE twits.origination > ( " +
								" SELECT origination FROM " + TableNames.TWEETS +
								" WHERE tweetId = " + tweet +
								" AND twits.stationId = " + station +
							" ) " +
							" ORDER BY twits.origination ASC " +
							" LIMIT 1"
			);

			if(results.next()){
				//TODO use column var
				videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), results.getString(1), results.getString(5), results.getString(7), results.getLong(6));
			}
		}

		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(tweet, station);
		}
		LOG.info("Retrieved video", videoForDisplay);
		return videoForDisplay;
	}

	private static VideoForDisplay getFillerVideo(Long tweet, Long station) throws SQLException
	{
		LOG.info("Getting filler video for {}", tweet);
		VideoForDisplay videoForDisplay = null;
		//TODO use prepared statement
		final String fillerSql = "SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId, vids.videoThumbnail " +
				" FROM " + TableNames.VIDEOS + " AS vids\n" +
				" INNER JOIN " + TableNames.TWEETS + " AS twits\n" +
				" ON vids.tweetId = twits.tweetId\n" +
				" INNER JOIN " + TableNames.STATIONS + " AS stn\n" +
				" ON twits.stationId = stn.stationId\n" +
				" WHERE stn.stationName = 'bpm_playlist'\n" +
				" AND twits.stationId = " + station +
				" AND DATE_SUB(NOW(), INTERVAL 2 HOUR) > twits.origination\n" +
				" AND twits.tweetId != " + tweet +
				" ORDER BY RAND()\n" +
				" LIMIT 1";
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final Statement statement = connection.createStatement())
		{
			ResultSet results = statement.executeQuery(fillerSql);

			if(results.next()){
				//TODO use column var
				videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), results.getString(1), results.getString(5), results.getString(7), results.getLong(6), tweet);
			}
		}

		if(videoForDisplay == null)
		{
			videoForDisplay = new VideoForDisplay("Never Gonna Give You Up", "Rick Astley", "Rick Astley - Never Gonna Give You Up", "dQw4w9WgXcQ", "Troll", null/*THUMBNAIL*/, null, tweet);
			//TODO temporarily adding null for tweet on these ones (easier for ui but should remove eventually)
		}
		return videoForDisplay;
	}
}
