package com.sxmt;

import com.sxmt.ui.VideoForDisplay;
import com.sxmt.ui.VideoRetriever;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class SXMTController {
	@RequestMapping(
            value="/stations",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public List<Station> stations() {
        List<Station> stations = new ArrayList<Station>();
        // TODO Populate list with stations (need a query for this)
        stations.add(new Station("BPM", "187541621")); //TEST
        return stations;
    }

	@RequestMapping(
            value="/nextSong",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Song nextSong(@RequestBody StationSong stationInfo) throws SQLException {
        final String station = stationInfo.getStation();
        final String lastSong = stationInfo.getLastSong();
        final String lastTweet = stationInfo.getLastTweet();
        final VideoForDisplay video;
        if (lastSong == null) {
            // send back latest song for station
            video = VideoRetriever.getNewestVideo();
//            video = new VideoForDisplay("Every Day", "Eric Prydz", "", "", "yOLd4jl0uQ8", "", 0L); //TEST
        } else {
            // send back next song in queue for station
            video = VideoRetriever.getNextVideo(Long.parseLong(lastTweet)); // TODO param needs to change to video id
//            video = new VideoForDisplay("Shadows", "Eric Prydz", "", "", "LD26X84KfD0", "", 0L); //TEST
        }
        return new Song(
                video.getArtist(),
                video.getSongName(),
                video.getVideoId(),
                video.getRelevantTweetId().toString()
        );
    }
}