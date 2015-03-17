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
            value="/song",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Song song(@RequestBody StationSong stationInfo) throws SQLException {
        final String station = stationInfo.getStation();
        final String song = stationInfo.getSong();
        final String tweet = stationInfo.getTweet();
        final int next = stationInfo.getNext();
        final VideoForDisplay video;
        if (song == null) {
            // send back latest song for station
            video = VideoRetriever.getNewestVideo();
//            video = new VideoForDisplay("Every Day", "Eric Prydz", "", "", "yOLd4jl0uQ8", "", 0L); //TEST
        } else {
            // next == 0 then send back the matching song else send back next song in queue for station
            video = VideoRetriever.getVideo(Long.parseLong(tweet), (next > 0));
//            video = new VideoForDisplay("Shadows", "Eric Prydz", "", "", "LD26X84KfD0", "", 0L); //TEST
        }
        Long tweetIdL = video.getRelevantTweetId(), referenceIdL = video.getReferenceTweetId();
        String tweetId = null, referenceId = null;
        if (tweetIdL != null) tweetId = tweetIdL.toString();
        if (referenceIdL != null) referenceId = referenceIdL.toString();
        return new Song(
                video.getArtist(),
                video.getSongName(),
                video.getVideoId(),
                tweetId,
                referenceId
        );
    }
}