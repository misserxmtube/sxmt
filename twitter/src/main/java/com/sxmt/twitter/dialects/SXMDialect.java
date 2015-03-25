package com.sxmt.twitter.dialects;

import java.util.Map;

/**
 * Dialect for stripping artist name and song title from a Twitter post.
 *
 * Created by remo.fischione on 3/14/2015.
 */
public interface SXMDialect
{
	public String getArtist(String postContent);
	public String getSongTitle(String postContent);
    public Map<String, String> getSongTitleAndArtist(String postContent);
}
