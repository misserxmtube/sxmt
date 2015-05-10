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
import java.util.LinkedList;
import java.util.List;

public class StationRetriever
{
	private static final Logger LOG = LoggerFactory.getLogger(StationRetriever.class);

	public static List<Station> getStations() throws SQLException
	{
		final String sql = " SELECT " + StationsFields.STATION_ID + ", " + StationsFields.STATION_NAME + ", " + StationsFields.STATION_HANDLE + ", " + StationsFields.STATION_THUMBNAIL + ", " + StationsFields.STATION_BACKDROP + " " +
				" FROM " + TableNames.STATIONS +
				" ORDER BY stationName ASC ";
		final List<Station> stations = new LinkedList<>();
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement preparedStatement = connection.prepareStatement(sql))
		{
			try (final ResultSet results = preparedStatement.executeQuery())
			{
				while (results.next())
				{
					stations.add(new Station(results.getLong(StationsFields.STATION_ID), results.getString(StationsFields.STATION_NAME), results.getString(StationsFields.STATION_HANDLE), results.getString(StationsFields.STATION_THUMBNAIL), results.getString(StationsFields.STATION_BACKDROP)));
				}
			}
		}

		return stations;
	}
}
