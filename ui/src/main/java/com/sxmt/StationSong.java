package com.sxmt;

/**
 * Station and last song information *
 */
public class StationSong {
    private String station;
    private String song;
    private String tweet;
    private int next;

    public StationSong() {
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String lastSong) {
        this.song = lastSong;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String lastTweet) {
        this.tweet = lastTweet;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }
}
