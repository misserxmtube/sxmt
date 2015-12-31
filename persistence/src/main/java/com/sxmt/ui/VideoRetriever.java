package com.sxmt.ui;

import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;
import com.sxmt.connection.TweetsFields;
import com.sxmt.connection.VideosFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
					final String fillerSql = "SELECT vids." + VideosFields.VIDEO_ID + ", twits." + TweetsFields.SONG_NAME + ", twits." + TweetsFields.ARTIST + ", vids." + VideosFields.VIDEO_TITLE + ", vids." + VideosFields.CHANNEL_NAME + ", twits." + TweetsFields.TWEET_ID + ", vids." + VideosFields.VIDEO_THUMBNAIL +
							" FROM " + TableNames.VIDEOS + " AS vids\n" +
							" INNER JOIN " + TableNames.TWEETS + " AS twits\n" +
							" ON vids.videoId = twits.videoId\n" +
							" WHERE twits.stationId = ?" +
							" AND DATE_SUB(NOW(), INTERVAL 2 HOUR) > twits.origination\n" +
							" ORDER BY RAND()\n" +
							" LIMIT 25";
					try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
							final PreparedStatement preparedStatement = connection.prepareStatement(fillerSql))
					{
						for(Station station : StationRetriever.getStations())
						{
							final List<FillerVideo> fillerVideoList = new ArrayList<>(25);
							final long stationId = station.getId();
							preparedStatement.setLong(1, stationId);
							try (final ResultSet results = preparedStatement.executeQuery())
							{
								final Set<String> alreadySeenVideos = new HashSet<>();
								while (results.next())
								{
									//add the video id to the list of already seen videos
									if(!alreadySeenVideos.contains(results.getString(VideosFields.VIDEO_ID)))
									{
										fillerVideoList.add(new FillerVideo(results.getString(TweetsFields.SONG_NAME), results.getString(TweetsFields.ARTIST), results.getString(VideosFields.VIDEO_TITLE), results.getString(VideosFields.VIDEO_ID), results.getString(VideosFields.CHANNEL_NAME), results.getString(VideosFields.VIDEO_THUMBNAIL), results.getLong(TweetsFields.TWEET_ID)));
										alreadySeenVideos.add(results.getString(VideosFields.VIDEO_ID));
									}
								}
							}

							if(fillerVideoList.isEmpty())
							{
								fillerVideoList.add(new FillerVideo("Never Gonna Give You Up", "Rick Astley", "Rick Astley - Never Gonna Give You Up", "dQw4w9WgXcQ", "Troll", null/*THUMBNAIL*/, null));
								//TODO temporarily adding null for tweet on these ones (easier for ui but should remove eventually)
							}

							stationFillerSongsMap.put(stationId, fillerVideoList);
						}
					}
				} catch (SQLException e)
				{
					LOG.error("Error getting filler videos!", e);
				}
			}
		};
		scheduler.scheduleAtFixedRate(populateFillerSongsMap, 0, 60, TimeUnit.SECONDS);
	}

	public static VideoForDisplay getNewestVideo(Long stationId) throws SQLException
	{
		LOG.info("Getting newest video for stationId {}", stationId);
		VideoForDisplay videoForDisplay = null;
		try
		{
			final String sql = " SELECT vids." + VideosFields.VIDEO_ID + ", twits." + TweetsFields.SONG_NAME + ", twits." + TweetsFields.ARTIST + ", vids." + VideosFields.VIDEO_TITLE + ", vids." + VideosFields.CHANNEL_NAME + ", twits." + TweetsFields.TWEET_ID + ", vids." + VideosFields.VIDEO_THUMBNAIL +
					" FROM " + TableNames.VIDEOS + " AS vids " +
					" INNER JOIN " + TableNames.TWEETS + " AS twits " +
					" ON twits.videoId = vids.videoId " +
					" AND twits.stationId = ?" +
					" ORDER BY twits.stationSongIndex DESC " +
					" LIMIT 1";
			try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
					final PreparedStatement preparedStatement = connection.prepareStatement(sql))
			{
				preparedStatement.setLong(1, stationId);
				try (final ResultSet results = preparedStatement.executeQuery())
				{
					while(results.next()){
						videoForDisplay = new VideoForDisplay(results.getString(TweetsFields.SONG_NAME), results.getString(TweetsFields.ARTIST), results.getString(VideosFields.VIDEO_TITLE), results.getString(VideosFields.VIDEO_ID), results.getString(VideosFields.CHANNEL_NAME), results.getString(VideosFields.VIDEO_THUMBNAIL), results.getLong(TweetsFields.TWEET_ID), null);
					}
				}

				if(videoForDisplay == null)
				{
					LOG.error("Could not get the newest video for station {} ... problem!", stationId);
					videoForDisplay = getFillerVideo(1L, stationId);
				}
			}
		} catch (SQLException e)
		{
			LOG.error("Error getting newest videos!", e);
		}
		LOG.info("Retrieved video: {}", videoForDisplay);
		return videoForDisplay;
	}

	public static VideoForDisplay getVideo(Long stationId, Long tweetId) throws SQLException
	{
		LOG.info("Getting video for stationId: {} tweetId: {}", stationId, tweetId);
		final String sql =
				" SELECT vids." + VideosFields.VIDEO_ID + ", twits." + TweetsFields.SONG_NAME + ", twits." + TweetsFields.ARTIST + ", vids." + VideosFields.VIDEO_TITLE + ", vids." + VideosFields.CHANNEL_NAME + ", twits." + TweetsFields.TWEET_ID + ", vids." + VideosFields.VIDEO_THUMBNAIL +
						" FROM " + TableNames.VIDEOS + " AS vids " +
						" INNER JOIN " + TableNames.TWEETS + " AS twits " +
						" ON twits.videoId = vids.videoId " +
						" WHERE twits.tweetId = ?";
		VideoForDisplay videoForDisplay = null;
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
	        preparedStatement.setLong(1, tweetId);
            try (final ResultSet results = preparedStatement.executeQuery())
            {
	            if(results.next()){
		            videoForDisplay = new VideoForDisplay(results.getString(TweetsFields.SONG_NAME), results.getString(TweetsFields.ARTIST), results.getString(VideosFields.VIDEO_TITLE), results.getString(VideosFields.VIDEO_ID), results.getString(VideosFields.CHANNEL_NAME), results.getString(VideosFields.VIDEO_THUMBNAIL), results.getLong(TweetsFields.TWEET_ID), null);
	            }

            }
        }

		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(tweetId, stationId);
		}
		LOG.info("Retrieved video", videoForDisplay);
        return videoForDisplay;
	}

	public static VideoForDisplay getNextVideo(Long stationId, Long tweetId) throws SQLException
	{
		LOG.info("Getting next video for stationId: {} tweetId: {}", stationId, tweetId);
		final String sql = " SELECT vids." + VideosFields.VIDEO_ID + ", twits." + TweetsFields.SONG_NAME + ", twits." + TweetsFields.ARTIST + ", vids." + VideosFields.VIDEO_TITLE + ", vids." + VideosFields.CHANNEL_NAME + ", twits." + TweetsFields.TWEET_ID + ", vids." + VideosFields.VIDEO_THUMBNAIL +
				" FROM " + TableNames.VIDEOS + " AS vids " +
				" INNER JOIN " + TableNames.TWEETS + " AS twits " +
				" ON twits.videoId = vids.videoId " +
				" WHERE twits.stationSongIndex = ( " +
					" SELECT stationSongIndex+1 FROM " + TableNames.TWEETS +
					" WHERE tweetId = ?" +
				" ) " +
				" AND twits.stationId = ?" +
				" LIMIT 1";
		VideoForDisplay videoForDisplay = null;
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement preparedStatement = connection.prepareStatement(sql))
		{
			preparedStatement.setLong(1, tweetId);
			preparedStatement.setLong(2, stationId);
			try (final ResultSet results = preparedStatement.executeQuery())
			{
				if(results.next()){
					videoForDisplay = new VideoForDisplay(results.getString(TweetsFields.SONG_NAME), results.getString(TweetsFields.ARTIST), results.getString(VideosFields.VIDEO_TITLE), results.getString(VideosFields.VIDEO_ID), results.getString(VideosFields.CHANNEL_NAME), results.getString(VideosFields.VIDEO_THUMBNAIL), results.getLong(TweetsFields.TWEET_ID), null);
				}
			}
		}

		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(tweetId, stationId);
		}
		LOG.info("Retrieved video", videoForDisplay);
		return videoForDisplay;
	}

	public static VideoForDisplay getPrevVideo(Long stationId, Long tweetId) throws SQLException
	{
		LOG.info("Getting prev video for stationId: {} tweetId: {}", stationId, tweetId);
		final String sql = " SELECT vids." + VideosFields.VIDEO_ID + ", twits." + TweetsFields.SONG_NAME + ", twits." + TweetsFields.ARTIST + ", vids." + VideosFields.VIDEO_TITLE + ", vids." + VideosFields.CHANNEL_NAME + ", twits." + TweetsFields.TWEET_ID + ", vids." + VideosFields.VIDEO_THUMBNAIL +
				" FROM " + TableNames.VIDEOS + " AS vids " +
				" INNER JOIN " + TableNames.TWEETS + " AS twits " +
				" ON twits.videoId = vids.videoId " +
				" WHERE twits.stationSongIndex = ( " +
					" SELECT stationSongIndex-1 FROM " + TableNames.TWEETS +
					" WHERE tweetId = ?" +
				" ) " +
				" AND twits.stationId = ?" +
				" LIMIT 1";
		VideoForDisplay videoForDisplay = null;
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement preparedStatement = connection.prepareStatement(sql))
		{
			preparedStatement.setLong(1, tweetId);
			preparedStatement.setLong(2, stationId);
			try (final ResultSet results = preparedStatement.executeQuery())
			{
				if(results.next()){
					videoForDisplay = new VideoForDisplay(results.getString(TweetsFields.SONG_NAME), results.getString(TweetsFields.ARTIST), results.getString(VideosFields.VIDEO_TITLE), results.getString(VideosFields.VIDEO_ID), results.getString(VideosFields.CHANNEL_NAME), results.getString(VideosFields.VIDEO_THUMBNAIL), results.getLong(TweetsFields.TWEET_ID), null);
				}
			}
		}

		if(videoForDisplay == null)
		{
			videoForDisplay = getFillerVideo(tweetId, stationId);
		}
		LOG.info("Retrieved video", videoForDisplay);
		return videoForDisplay;
	}

	private static VideoForDisplay getFillerVideo(Long tweetId, Long stationId) throws SQLException
	{
		LOG.info("Getting filler video for stationId: {} tweetId: {}", stationId, tweetId);
		final List<FillerVideo> fillerVideos = stationFillerSongsMap.get(stationId);
		final FillerVideo fillerVideo = fillerVideos.get(rand.nextInt(fillerVideos.size()));
		return new VideoForDisplay(fillerVideo.getSongName(), fillerVideo.getArtist(), fillerVideo.getVideoTitle(), fillerVideo.getVideoId(), fillerVideo.getChannelName(), fillerVideo.getThumbnail(), fillerVideo.getRelevantTweetId(), tweetId);
	}
}
