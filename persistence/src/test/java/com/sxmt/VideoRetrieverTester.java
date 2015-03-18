package com.sxmt;

import com.sxmt.ui.VideoForDisplay;
import com.sxmt.ui.VideoRetriever;
import org.junit.Test;

import java.sql.SQLException;

public class VideoRetrieverTester
{
	@Test
	public void testGetVideo() throws SQLException
	{
		final VideoForDisplay video = VideoRetriever.getVideo(2624623423623452L, 247455247L);
		System.out.println(video);
	}

	@Test
	public void testGetNextVideo() throws SQLException
	{
		final VideoForDisplay nextVideo = VideoRetriever.getNextVideo(2624623423623452L, 247455247L);
		System.out.println(nextVideo);
	}

	@Test
	public void testGetNewestVideo() throws SQLException
	{
		final VideoForDisplay newestVideo = VideoRetriever.getNewestVideo(247455247L);
		System.out.println(newestVideo);
	}
}
