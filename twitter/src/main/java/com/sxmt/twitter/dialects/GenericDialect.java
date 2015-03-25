package com.sxmt.twitter.dialects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic regex dialect
 *
 * Created by doug.calderon on 3/24/2015.
 */
public class GenericDialect implements SXMDialect
{
    private Pattern pattern;
    private List<String> excludes;

    public GenericDialect(String regex, List<String> excludes)
    {
        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        this.excludes = excludes;
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
        System.out.println("Received postContent: "+ postContent);
        Matcher matcher = pattern.matcher(postContent);
        Map<String, String> map = new HashMap<String, String>();
        if(matcher.find())
        {
            String artist = matcher.group("artist").trim();
            String songTitle = matcher.group("songTitle").trim();
            for(String exclude : excludes){
                artist = artist.replaceAll(exclude, "");
                songTitle = songTitle.replaceAll(exclude, "");
            }
            map.put("artist", artist);
            map.put("songTitle", songTitle);
        } else
        {
            System.out.println("Error parsing tweet: \""+postContent+"\"");
            map.put("artist", "");
            map.put("songTitle", "");
        }

        return map;
    }
}
