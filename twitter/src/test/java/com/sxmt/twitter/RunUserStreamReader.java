package com.sxmt.twitter;

public class RunUserStreamReader
{
	public static void main(String[] args)
	{
		Long userID = 3089407595L;	// @missersxmtube
		UserStreamReader reader = new UserStreamReader();
		reader.addUser(userID);

		try
		{
			reader.run();
			Thread.sleep(30000);
			reader.stop();
		} catch (Exception e)
		{
			System.out.println("error rarararara");
		}
	}
}
