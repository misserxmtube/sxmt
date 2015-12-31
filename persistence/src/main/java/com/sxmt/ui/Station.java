package com.sxmt.ui;

import java.util.Map;

/** Station information **/
public class Station
{
	private Long id;
	private String name;
	private String handle;
	private String thumbnail;
	private String backdrop;
	private Map<String, Integer> genres;

	public Station(Long id, String name, String handle, String thumbnail, String backdrop) {
		this.id = id;
		this.name = name;
		this.handle = handle;
		this.thumbnail = thumbnail;
		this.backdrop = backdrop;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHandle()
	{
		return handle;
	}

	public void setHandle(String handle)
	{
		this.handle = handle;
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

	public Map<String, Integer> getGenres() {
		return genres;
	}

	public void setGenres(Map<String, Integer> genres) {
		this.genres = genres;
	}
}
