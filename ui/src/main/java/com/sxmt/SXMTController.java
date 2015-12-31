package com.sxmt;

import com.sxmt.ui.Station;
import com.sxmt.ui.StationRetriever;
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
    public List<Station> getAllStations() throws SQLException {
        return StationRetriever.getStations();
    }

	@RequestMapping(
            value="/song",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Song getSongForStation(@RequestBody StationSong stationInfo) throws SQLException {
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
        final Long tweetIdL = video.getRelevantTweetId();
        final Long referenceIdL = video.getReferenceTweetId();
        String tweetId = null;
        String referenceId = null;
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

	@RequestMapping(
            value = "/song",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
	public List<Song> getAllNewestSongs() throws SQLException {
        final List<Song> allSongs = new ArrayList<>();
        final List<Station> allStations = getAllStations();
		for (Station s : allStations)
		{
            final String stationId = String.valueOf(s.getId());
            final StationSong stationSong = new StationSong();
			stationSong.setStation(stationId);
			allSongs.add(getSongForStation(stationSong));
		}
		return allSongs;
	}

	@RequestMapping(
			value="/song/previous",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Song getPreviousSongForStation(@RequestBody StationSong stationInfo) throws SQLException {
		// todo wait for persistence layer implementation
		return null;
	}
}