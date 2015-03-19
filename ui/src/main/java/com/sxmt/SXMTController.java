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
        stations.add(new Station("BPM", "247455247", "http://i.imgur.com/fxOJROV.jpg", "http://i.imgur.com/fLVXu6r.png")); //TEST
        return stations;
    }

	@RequestMapping(
            value="/song",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Song song(@RequestBody StationSong stationInfo) throws SQLException {
        final Long station = Long.parseLong(stationInfo.getStation());
        final String song = stationInfo.getSong();
        final String tweet = stationInfo.getTweet();
        final int next = stationInfo.getNext();
        final VideoForDisplay video;
        if (song == null) {
            // send back latest song for station
            video = VideoRetriever.getNewestVideo(station);
        } else if (next > 0) {
            // send back next song in queue for station
            video = VideoRetriever.getNextVideo(station, Long.parseLong(tweet));
        } else {
            // send back the matching song
            video = VideoRetriever.getVideo(station, Long.parseLong(tweet));
        }
        Long tweetIdL = video.getRelevantTweetId(), referenceIdL = video.getReferenceTweetId();
        String tweetId = null, referenceId = null;
        if (tweetIdL != null) tweetId = tweetIdL.toString();
        if (referenceIdL != null) referenceId = referenceIdL.toString();
        return new Song( // Getting close to actually replacing this with VideoForDisplay
                video.getVideoTitle(),
                video.getArtist(),
                video.getSongName(),
                video.getVideoId(),
                video.getThumbnail(),
                tweetId,
                referenceId
        );
    }
}