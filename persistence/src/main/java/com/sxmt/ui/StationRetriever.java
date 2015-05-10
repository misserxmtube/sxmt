package com.sxmt.ui;

import com.sxmt.connection.SQLConnectionFactory;
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
		final String sql = " SELECT stationId, stationName, stationHandle, stationThumbnail, stationBackdrop " +
				" FROM " + TableNames.STATIONS +
				" ORDER BY stationName ASC ";
		final List<Station> stations = new LinkedList<>();
		try (final Connection connection = SQLConnectionFactory.newMySQLConnection();
				final PreparedStatement preparedStatement = connection.prepareStatement(sql))
		{
			//TODO use prepared statement
			try (final ResultSet results = preparedStatement.executeQuery())
			{
				while (results.next())
				{
					//TODO use column var
					stations.add(new Station(results.getLong(1), results.getString(2), results.getString(3), results.getString(4), results.getString(5)));
				}
			}
		}

		return stations;
	}
}
