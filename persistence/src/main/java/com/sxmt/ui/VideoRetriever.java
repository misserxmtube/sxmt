package com.sxmt.ui;

import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VideoRetriever
{
	private static final Logger LOG = LoggerFactory.getLogger(VideoRetriever.class);

	private static final ConcurrentHashMap<Long, List<FillerVideo>> stationFillerSongsMap = new ConcurrentHashMap<>();

	private static final Random rand = new Random();

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	static {
		final Runnable populateFillerSongsMap = new Runnable() {
			public void run() {
				try
				{
					for(Station station : StationRetriever.getStations())
					{
						List<FillerVideo> fillerVideoList = new ArrayList<>(25);
						final String fillerSql = "SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId, vids.videoThumbnail " +
								" FROM " + TableNames.VIDEOS + " AS vids\n" +
								" INNER JOIN " + TableNames.TWEETS + " AS twits\n" +
								" ON vids.tweetId = twits.tweetId\n" +
								" WHERE twits.stationId = ?" +
								" AND DATE_SUB(NOW(), INTERVAL 2 HOUR) > twits.origination\n" +
								" ORDER BY RAND()\n" +
								" LIMIT 25";
						try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
								final PreparedStatement preparedStatement = connection.prepareStatement(fillerSql))
						{
							preparedStatement.setLong(1, station.getId());
							final ResultSet results = preparedStatement.executeQuery();

							while(results.next()) {
								//TODO use column var
								fillerVideoList.add(new FillerVideo(results.getString(2), results.getString(3), results.getString(4), results.getString(1), results.getString(5), results.getString(7), results.getLong(6)));
							}
						}

						if(fillerVideoList.isEmpty())
						{
							fillerVideoList.add(new FillerVideo("Never Gonna Give You Up", "Rick Astley", "Rick Astley - Never Gonna Give You Up", "dQw4w9WgXcQ", "Troll", null/*THUMBNAIL*/, null));
							//TODO temporarily adding null for tweet on these ones (easier for ui but should remove eventually)
						}

						stationFillerSongsMap.put(station.getId(), fillerVideoList);
					}
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		};
		scheduler.scheduleAtFixedRate(populateFillerSongsMap, 0, 60, TimeUnit.SECONDS);
	}

	public static VideoForDisplay getNewestVideo(Long stationId) throws SQLException
	{
		LOG.info("Getting newest video");
		final String sql = " SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId, vids.videoThumbnail FROM " + TableNames.VIDEOS + " AS vids " +
				" INNER JOIN " + TableNames.TWEETS + " AS twits " +
				" ON twits.tweetId = vids.tweetId " +
				" AND twits.stationId = ?" +
				" ORDER BY twits.origination DESC " +
				" LIMIT 1";
		VideoForDisplay videoForDisplay = null;
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
	        preparedStatement.setLong(1, stationId);
            try (final ResultSet results = preparedStatement.executeQuery())
            {
	            while(results.next()){
		            //TODO use column var
		            videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), results.getString(1), results.getString(5), results.getString(7), results.getLong(6), null);
	            }
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
		LOG.info("Getting video for station: {} tweet: {}", station, tweet);
		final String sql =
				" SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId, vids.videoThumbnail FROM " + TableNames.VIDEOS + " AS vids " +
						" INNER JOIN " + TableNames.TWEETS + " AS twits " +
						" ON twits.tweetId = vids.tweetId " +
						" WHERE twits.tweetId = ?" +
						" AND twits.stationId = ?";
		VideoForDisplay videoForDisplay = null;
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
	        preparedStatement.setLong(1, tweet);
	        preparedStatement.setLong(2, station);
            try (final ResultSet results = preparedStatement.executeQuery())
            {
	            if(results.next()){
		            //TODO use column var
		            videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), results.getString(1), results.getString(5), results.getString(7), results.getLong(6), null);
	            }

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
		LOG.info("Getting next video for station: {} tweet: {}", station, tweet);
		final String sql = " SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId, vids.videoThumbnail FROM " + TableNames.VIDEOS + " AS vids " +
				" INNER JOIN " + TableNames.TWEETS + " AS twits " +
				" ON twits.tweetId = vids.tweetId " +
				" WHERE twits.origination > ( " +
					" SELECT origination FROM " + TableNames.TWEETS +
					" WHERE tweetId = ?" +
					" AND twits.stationId = ?" +
				" ) " +
				" ORDER BY twits.origination ASC " +
				" LIMIT 1";
		VideoForDisplay videoForDisplay = null;
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement preparedStatement = connection.prepareStatement(sql))
		{
			preparedStatement.setLong(1, tweet);
			preparedStatement.setLong(2, station);
			try (final ResultSet results = preparedStatement.executeQuery())
			{
				if(results.next()){
					//TODO use column var
					videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), results.getString(1), results.getString(5), results.getString(7), results.getLong(6), null);
				}
			}
		}

		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(tweet, station);
		}
		LOG.info("Retrieved video", videoForDisplay);
		return videoForDisplay;
	}

	public static VideoForDisplay getPrevVideo(Long station, Long tweet) throws SQLException
	{
		LOG.info("Getting prev video for station: {} tweet: {}", station, tweet);
		final String sql = " SELECT vids.videoId, twits.songName, twits.artist, vids.videoTitle, vids.channelName, twits.tweetId, vids.videoThumbnail FROM " + TableNames.VIDEOS + " AS vids " +
				" INNER JOIN " + TableNames.TWEETS + " AS twits " +
				" ON twits.tweetId = vids.tweetId " +
				" WHERE twits.origination < ( " +
				" SELECT origination FROM " + TableNames.TWEETS +
				" WHERE tweetId = ?" +
				" AND twits.stationId = ?" +
				" ) " +
				" ORDER BY twits.origination DESC " +
				" LIMIT 1";
		VideoForDisplay videoForDisplay = null;
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement preparedStatement = connection.prepareStatement(sql))
		{
			preparedStatement.setLong(1, tweet);
			preparedStatement.setLong(2, station);
			try (final ResultSet results = preparedStatement.executeQuery())
			{
				if(results.next()){
					//TODO use column var
					videoForDisplay = new VideoForDisplay(results.getString(2), results.getString(3), results.getString(4), results.getString(1), results.getString(5), results.getString(7), results.getLong(6), null);
				}
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
		LOG.info("Getting filler video for station: {} tweet: {}", station, tweet);
		final List<FillerVideo> fillerVideos = stationFillerSongsMap.get(station);
		final FillerVideo fillerVideo = fillerVideos.get(rand.nextInt(fillerVideos.size()));
		return new VideoForDisplay(fillerVideo.getSongName(), fillerVideo.getArtist(), fillerVideo.getVideoTitle(), fillerVideo.getVideoId(), fillerVideo.getChannelName(), fillerVideo.getThumbnail(), fillerVideo.getRelevantTweetId(), tweet);
	}
}
