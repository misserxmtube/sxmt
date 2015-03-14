package com.sxmt;

import org.joda.time.DateTime;

/**
 * Created by douglas.calderon on 3/13/2015.
 */
public class YoutubeRecord
{

    private String videoId;
    private DateTime publishDate;
    private String title;
    private String description;

    private String jsonBlob;

    public String getVideoId()
    {
        return videoId;
    }

    public void setVideoId(String videoId)
    {
        this.videoId = videoId;
    }

    public DateTime getPublishDate()
    {
        return publishDate;
    }

    public void setPublishDate(DateTime publishDate)
    {
        this.publishDate = publishDate;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getJsonBlob()
    {
        return jsonBlob;
    }

    public void setJsonBlob(String jsonBlob)
    {
        this.jsonBlob = jsonBlob;
    }
}
