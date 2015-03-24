package com.sxmt.twitter.dialects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dialect for Sirius XM BPM Twitter feed.
 *
 * Template: "${artist} - ${song} playing on #BPM - @sxmElectro"
 *
 * Created by remo.fischione on 3/14/2015.
 */
public class GenericDialect implements SXMDialect
{
    private Pattern pattern;

    public GenericDialect(String regex)
    {

    }

    @Override
    public String getArtist(String postContent)
    {
        return getSongTitleAndArtist(postContent).get("artist");
    }

    @Override
    public String getSongTitle(String postContent)
    {
        return getSongTitleAndArtist(postContent).get("songTitle");
    }

    /**
     * Returns a Map with two values, artist and songTitle
     * @param postContent
     * @return Map with two values, artist and songTitle
     */
    @Override
    public Map<String, String> getSongTitleAndArtist(String postContent)
    {
        Matcher matcher = pattern.matcher(postContent);
        Map<String, String> map = new HashMap<String, String>();
        if(matcher.groupCount() < 2)
        {
            System.out.println("Error parsing tweet: \""+postContent+"\"");
            map.put("artist", "");
            map.put("songTitle", "");
        } else
        {
            map.put("artist", matcher.group(1));
            map.put("songTitle", matcher.group(2));
            //TODO MAYBE TWO REGEX?
        }

        return map;
    }
}
