package com.sxmt;

/** Station information **/
public class Station {
    private String id;
    private String name;
    private String thumbnail;
    private String backdrop;

    public Station(String name, String id, String thumbnail, String backdrop) {
        this.name = name;
        this.id = id;
        this.thumbnail = thumbnail;
        this.backdrop = backdrop;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }
}
