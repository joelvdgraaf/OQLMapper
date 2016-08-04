package oqlmapper.implementation;

import java.util.ArrayList;
import java.util.List;

public class Template
{
	private String OQLQuery;
	private String ParentObject;
	private String Association;
	private String RowObject;
	private List<Mapping> mappingList;
	private String contextToken;
	
	public Template(String OQLQuery, String ParentObject, String association, String rowObject, String contextToken)
	{		
		this.OQLQuery = OQLQuery;
		this.ParentObject = ParentObject;
		this.Association = association;
		this.RowObject = rowObject;
		this.mappingList = new ArrayList<Mapping>();
		this.contextToken = contextToken;
	}
	
	public void addMapping(Mapping mapping)
	{
		this.mappingList.add(mapping);
	}

	public String getOQLQuery()
	{
		return OQLQuery;
	}	

	public String getParentObject()
	{
		return ParentObject;
	}

	public String getAssociation()
	{
		return Association;
	}

	public String getRowObject()
	{
		return RowObject;
	}

	public List<Mapping> getMappingList()
	{
		return mappingList;
	}	
	
	public String getContextToken(){
		return this.contextToken;
	}
}
