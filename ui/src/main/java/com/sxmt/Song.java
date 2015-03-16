package com.sxmt;

/** Reduced video information intermediary **/
public class Song {
    private String id;
    private String song;
    private String artist;
    private String tweet;

    public Song() {}

    public Song(String artist, String song, String id, String tweet) {
        this.artist = artist;
        this.song = song;
        this.id = id;
        this.tweet = tweet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
