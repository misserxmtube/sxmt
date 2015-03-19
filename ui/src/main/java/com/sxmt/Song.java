package com.sxmt;

/** Reduced video information intermediary **/
public class Song {
    private String id;
    private String title;
    private String song;
    private String artist;
    private String tweet;
    private String referenceTweet;

    public Song() {}

    public Song(String title, String artist, String song, String id, String tweet, String referenceTweet) {
        this.title = title;
        this.artist = artist;
        this.song = song;
        this.id = id;
        this.tweet = tweet;
        this.referenceTweet = referenceTweet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public String getReferenceTweet() {
        return referenceTweet;
    }

    public void setReferenceTweet(String referenceTweet) {
        this.referenceTweet = referenceTweet;
    }
}
