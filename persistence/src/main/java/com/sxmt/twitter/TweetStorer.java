package com.sxmt.twitter;

import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.TableNames;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TweetStorer
{
	public static void storeTweet(Tweet tweet) throws SQLException
	{
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final Statement statement = connection.createStatement())
		{
			//insert user if not exists
			statement.executeQuery("INSERT IGNORE (userId, userName, handle) VALUES (" + tweet.getUserId() + "," + tweet.getUserName() + "," + tweet.getUserHandle() + ")");

			//insert the 'tweet'
			final String query = "INSERT (tweetId, userId, twitterText, songName, artist, origination, jsonBlob) INTO " + TableNames.TWEETS + " VALUES (" +
					tweet.getId() + "," + tweet.getUserId() + "," + tweet.getTweetText() + "," + tweet.getSongName() + "," + tweet.getArtist() + "," + tweet.getOrigination() + "," + tweet.getBlob() + ")";
			statement.executeQuery(query);
		}


	}
}
