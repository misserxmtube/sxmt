package com.sxmt;

/** Station and last song information **/
public class StationSong {
    private String station;
    private String lastSong;
    private String lastTweet; //oi

    public StationSong() {}

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getLastSong() {
        return lastSong;
    }

    public void setLastSong(String lastSong) {
        this.lastSong = lastSong;
    }

    public String getLastTweet() {
        return lastTweet;
    }

    public void setLastTweet(String lastTweet) {
        this.lastTweet = lastTweet;
    }
}
