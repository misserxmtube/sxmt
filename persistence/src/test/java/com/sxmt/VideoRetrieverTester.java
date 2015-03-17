package com.sxmt;

import com.sxmt.ui.VideoForDisplay;
import com.sxmt.ui.VideoRetriever;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Class VideoRetrieverTester
 *
 * @since 03/14/2015
 */
public class VideoRetrieverTester
{
	@Test
	public void testGetVideo() throws SQLException
	{
		final VideoForDisplay nextVideo = VideoRetriever.getVideo(2624623423623452L);
		System.out.println(nextVideo);
	}

	@Test
	public void testGetNewestVideo() throws SQLException
	{
		final VideoForDisplay newestVideo = VideoRetriever.getNewestVideo();
		System.out.println(newestVideo);
	}
}
