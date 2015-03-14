package com.sxmt.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Properties
{
	private static final Logger LOG = LoggerFactory.getLogger(Properties.class);

	//Config file that should be located in the resources folder
	private static final String CONFIG_FILE_NAME = "persistence.properties";

	//Singleton
	private static Properties instance = null;

	private final String databaseURL;
	private final String databaseUsername;
	private final String databasePassword;
	private final String appDatabaseName;

	private Properties()
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		java.util.Properties properties = new java.util.Properties();
		try (InputStream resourceStream = loader.getResourceAsStream(CONFIG_FILE_NAME))
		{
			properties.load(resourceStream);
			databaseURL = properties.getProperty("databaseURL");
			databaseUsername = properties.getProperty("databaseUsername");
			databasePassword = properties.getProperty("databasePassword");
			appDatabaseName = properties.getProperty("appDatabaseName");
		} catch (FileNotFoundException e)
		{
			LOG.error("Properties file does not exist", e);
			throw new PropertiesException(e);
		} catch (IOException e)
		{
			LOG.error("Error loading properties", e);
			throw new PropertiesException(e);
		}
	}

	public static Properties getInstance()
	{
		return instance == null ? new Properties() : instance;
	}

	public String getDatabaseURL()
	{
		return databaseURL;
	}

	public String getDatabaseUsername()
	{
		return databaseUsername;
	}

	public String getDatabasePassword()
	{
		return databasePassword;
	}

	public String getAppDatabaseName()
	{
		return appDatabaseName;
	}
}
