package com.sxmt.connection;

import com.sxmt.config.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnectionFactory
{
	static
	{
		JDBCLoader.loadMySQLJDBCDriver();
	}

	public static Connection newMySQLConnection(String databaseURL, String databaseUsername, String databasePassword) throws SQLException
	{
		final String argumentDelimiter;
		if(databaseURL.contains("?")) {
			argumentDelimiter = "&";
		} else {
			argumentDelimiter = "?";
		}
		return DriverManager.getConnection(databaseURL + argumentDelimiter + "user=" + databaseUsername + "&password=" + databasePassword);
	}

	public static Connection newMySQLConnection() throws SQLException
	{
		final Properties propertyHolder = Properties.getInstance();
		return newMySQLConnection(propertyHolder.getDatabaseURL(), propertyHolder.getDatabaseUsername(), propertyHolder.getDatabasePassword());
	}
}
