package com.sxmt.twitter.dialects;

/**
 * Dialect for Sirius XM BPM Twitter feed.
 *
 * Template: "${artist} - ${song} playing on #BPM - @sxmElectro"
 *
 * Created by remo.fischione on 3/14/2015.
 */
public class BPMDialect implements SXMDialect
{
	@Override
	public String getArtist(String postContent)
	{
		int dash = postContent.indexOf('-');
		return postContent.substring(0, dash).trim();
	}

	@Override
	public String getSongTitle(String postContent)
	{
		int po = postContent.indexOf("playing on");
		int dash = postContent.indexOf('-');
		return postContent.substring(dash+1, po).trim();
	}

    @Override
    public Map<String, String> getSongTitleAndArtist(String postContent)
    {

    }
}
