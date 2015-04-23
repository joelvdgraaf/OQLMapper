package oqlmapper.implementation;

import oqlmapper.proxies.DataType;

public class Mapping
{
	private String aliasName;
	private String attributeName;
	private DataType parseType;
	
	public Mapping(String aliasName, String attributeName, DataType parseType)
	{		
		this.aliasName = aliasName;
		this.attributeName = attributeName;
		this.parseType = parseType;
	}

	public String getAliasName()
	{
		return aliasName;
	}

	public String getAttributeName()
	{
		return attributeName;
	}

	public DataType getParseType()
	{
		return parseType;
	}
			
}
