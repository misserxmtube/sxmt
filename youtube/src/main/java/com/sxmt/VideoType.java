package com.sxmt;

public enum VideoType
{
	NORMAL("NORMAL"),
	SAFE("SAFE");

	private String typeName;

	private VideoType(String typeName)
	{
		this.typeName = typeName;
	}

	public String getTypeName()
	{
		return typeName;
	}
}
