package com.sxmt.twitter;

import com.sxmt.twitter.dialects.BPMDialect;

public class RunUserStreamReader
{
	public static void main(String[] args) throws Exception
	{
		Long userID = 3089407595L;	// @missersxmtube
		UserStreamReader reader = new UserStreamReader();
		reader.addUser(userID, new BPMDialect());

		Thread runner = new Thread(reader);
		runner.start();
		long patience = 1000 * 12;
		long startTime = System.currentTimeMillis();
		while (runner.isAlive()) {
			System.out.println("time: " + ((System.currentTimeMillis() - startTime) > patience));
			// Wait maximum of 1 second
			// for MessageLoop thread
			// to finish.
			runner.join(1000);
			if (((System.currentTimeMillis() - startTime) > patience)
					&& runner.isAlive()) {
				runner.interrupt();
				// Shouldn't be long now
				// -- wait indefinitely
				runner.join();
			}
		}
	}
}
