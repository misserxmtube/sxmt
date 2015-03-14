package com.sxmt.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCLoader
{
	private static final Logger LOG = LoggerFactory.getLogger(JDBCLoader.class);

	// mysql JDBC driver name
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	private static boolean loaded = false;

	public static void loadMySQLJDBCDriver() {
		if(!loaded) {
			try {
				Class.forName(JDBC_DRIVER).newInstance();
				loaded = true;
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
			{
				LOG.error("JDBC Setup error", e);
				throw new RuntimeException(e);
			}
		}
	}
}
