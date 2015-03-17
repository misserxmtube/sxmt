package com.sxmt;

import org.joda.time.DateTime;

/**
 * Created by douglas.calderon on 3/13/2015.
 */
public class YoutubeRecord
{
    private String videoId;
    private String title;
    private String channelTitle;
    private DateTime publishDate;
    private String thumbnail;
    private int duration;

    public YoutubeRecord(String videoId, String title, String channelTitle, DateTime publishDate, String thumbnail)
    {
        this.videoId = videoId;
        this.title = title;
        this.channelTitle = channelTitle;
        this.publishDate = publishDate;
        this.thumbnail = thumbnail;
    }

    public DateTime getPublishDate()
    {
        return publishDate;
    }

    public void setPublishDate(DateTime publishDate)
    {
        this.publishDate = publishDate;
    }

    public String getVideoId()
    {
        return videoId;
    }

    public void setVideoId(String videoId)
    {
        this.videoId = videoId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getChannelTitle()
    {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle)
    {
        this.channelTitle = channelTitle;
    }

    public String getThumbnail()
    {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }
}
