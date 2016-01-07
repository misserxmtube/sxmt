package com.sxmt.ui;

import com.sxmt.connection.SQLConnectionFactory;
import com.sxmt.connection.StationsFields;
import com.sxmt.connection.TableNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StationRetriever {
    private static final Logger LOG = LoggerFactory.getLogger(StationRetriever.class);

    public static List<Station> getStations() throws SQLException {
        final String getStationsSQL = " SELECT " + StationsFields.STATION_ID + ", " + StationsFields.STATION_NAME + ", " + StationsFields.STATION_HANDLE + ", " + StationsFields.STATION_THUMBNAIL + ", " + StationsFields.STATION_BACKDROP + " " +
                " FROM " + TableNames.STATIONS +
                " ORDER BY stationName ASC ";
        final List<Station> stations = new LinkedList<>();
        try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(getStationsSQL)) {
            try (final ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    stations.add(
                        new Station(
                            results.getLong(StationsFields.STATION_ID)
                            , results.getString(StationsFields.STATION_NAME)
                            , results.getString(StationsFields.STATION_HANDLE)
                            , results.getString(StationsFields.STATION_THUMBNAIL)
                            , results.getString(StationsFields.STATION_BACKDROP)
                        )
                    );
                }
            }
        }

        for(Station station: stations){
            station.setGenres(getStationGenres(station.getName()));
        }
        return stations;
    }

    public static Map<String, Integer> getStationGenres(String stationName) throws SQLException {
        final String getStationGenresSQL = "" +
                "SELECT ge.genre as genre, count(*) as count " +
                "FROM sxmt.stations st " +
                "INNER JOIN sxmt.tweets tw " +
                "   ON tw.stationId = st.stationId " +
                "INNER JOIN sxmt.artistGenres ar " +
                "   ON ar.artistId = tw.artistId " +
                "INNER JOIN sxmt.genres ge " +
                "   ON ar.genreId = ge.genreId " +
                "where st.stationName = ? " +
                "group by ge.genre " +
                "order by count(*) desc; ";
        final Map<String, Integer> stationGenres = new LinkedHashMap<>(); //preserves insert order
        try(
            final Connection connection = SQLConnectionFactory.newMySQLConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(getStationGenresSQL)
        ){
            preparedStatement.setString(1, stationName);
            try(final ResultSet results = preparedStatement.executeQuery()) {
                while(results.next()){
                    stationGenres.put(results.getString("genre"), results.getInt("count"));
                }
            }
        }

        return stationGenres;
    }
}
