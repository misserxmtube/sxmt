package com.sxmt;

import com.sxmt.twitter.Tweet;
import com.sxmt.twitter.TweetStorer;
import org.joda.time.DateTime;
import org.junit.Test;

import java.sql.SQLException;

/**
 * Unit test for simple App.
 */
public class TweetStorerTest
{
	@Test
	public void storeTweetTest() throws SQLException
    {
	    final Tweet tweet = new Tweet(1234232L, "levels", "avicii", "misserbear", "@missermisser", "YOLO SWAG AVICII LEVELS 420", 1023747237L, new DateTime(), "BODASDAUFOUASDROUAUFIUIFAHUIGAFOGAYGFYAYGFSDAYGOASDOIYGIYGOADSIYOGADSASIYOFAGSIFGASIU");
	    TweetStorer.storeTweet(tweet);
    }
}
