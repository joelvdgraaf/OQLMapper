package oqlmapper.implementation;

import java.util.Date;
import java.util.HashMap;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.connectionbus.data.IDataRow;
import com.mendix.systemwideinterfaces.connectionbus.data.IDataTable;
import com.mendix.systemwideinterfaces.connectionbus.requests.types.IOQLTextGetRequest;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixIdentifier;
import com.mendix.systemwideinterfaces.core.IMendixObject;


public class QueryExecution {
	private static ILogNode log = Core.getLogger("OQLMapper");
	private static HashMap<String, Template> templateMap = new HashMap<String, Template>();
	
	private Template template;
	private String OQLQuery;
	private IMendixObject parentObj;
	private boolean parentObjOwnerAssociation;

	
	
	public static void addTemplate(String templateName, Template template) {
		templateMap.put(templateName, template);
		log.info("The template with name '" + templateName + "' has been added in the memory.");
	}

	public static Template getTemplate(String templateName) {
		return templateMap.get(templateName);
	}
	
	public QueryExecution(String templateName, String OQLQuery, IMendixObject parentObj) throws CoreException {
		this.template = templateMap.get(templateName);
		if (this.template != null) {
			if (OQLQuery != null && !"".equals(OQLQuery))
				this.OQLQuery = OQLQuery;
			else
				this.OQLQuery = this.template.getOQLQuery();
			log.debug("Retrieved request for template: " + templateName);
		} else {
			throw new CoreException("The requested template '" + templateName
					+ "' wasn't foud in memory so the query can't be executed. Please check if the templates are loaded in memory and check your log.");
		}

		if (parentObj != null) {
			if (Core.isSubClassOf(this.template.getParentObject(), parentObj.getType())) {
				this.parentObj = parentObj;
				this.parentObjOwnerAssociation = this.parentObj.hasMember(this.template.getAssociation());
			} else {
				throw new CoreException("The given parent Object '" + parentObj.getType()
						+ "' isn't equal or a specialization of the expected type: " + this.template.getParentObject());
			}
		} else {
			throw new CoreException("The requested template '" + templateName
					+ "' could not be executed because the parent object wasn't given.");
		}
	}

	public boolean executeQuery(IContext context, IMendixObject contextObj) throws CoreException {
		if (contextObj != null) {
			log.trace("Replacing contextobject tokens");
			
			if (this.template.getContextToken() != null){
				this.OQLQuery = this.OQLQuery.replace(this.template.getContextToken(), Long.toString(contextObj.getId().toLong()));
			}

			this.OQLQuery = this.OQLQuery.replace("'[%ContextObjectID%]'", Long.toString(contextObj.getId().toLong()));
			
		} else {
			log.trace("No contextobject given");
		}

		return this.executeQuery(context);
	}

	public boolean executeQuery(IContext context) throws CoreException {

		log.trace("Start of executing the OQL Query");
		IDataTable table = executeOQL(context);
		Iterator<IDataRow> resultIterator = table.iterator();
		List<IMendixIdentifier> rowList = new ArrayList<IMendixIdentifier>();
		log.debug("Query executed with a result size of: " + table.getRowCount());
		int rowCounter = 0;

		while (resultIterator.hasNext()) {
			IDataRow row = resultIterator.next();

			// Create object and set the association to the parent object as
			// reference
			IMendixObject rowObject = Core.instantiate(context, this.template.getRowObject());
			rowList.add(rowObject.getId());
			if (!this.parentObjOwnerAssociation)
				rowObject.setValue(context, this.template.getAssociation(), this.parentObj.getId());

			// Parse the data to the requested type
			for (Mapping map : this.template.getMappingList()) {
				Object value = row.getValue(context, map.getAliasName());
				if (value != null) {
					log.trace(rowCounter + " Parsing retrieved value for mapping: " + map.getAliasName()
							+ " with parse type: " + map.getParseType());
					switch (map.getParseType()) {
					case String:
						rowObject.setValue(context, map.getAttributeName(), (String) value);
						break;
					case Integer:
						rowObject.setValue(context, map.getAttributeName(), (Integer) value);
						break;
					case _Long:
						rowObject.setValue(context, map.getAttributeName(), (Long) value);
						break;
					case _Float:
						rowObject.setValue(context, map.getAttributeName(), (Double) value);
						break;
					case Decimal:
						rowObject.setValue(context, map.getAttributeName(), value);
						break;
					case _Boolean:
						rowObject.setValue(context, map.getAttributeName(), (Boolean) value);
						break;
					case DateTime:
						rowObject.setValue(context, map.getAttributeName(), (Date) value);
						break;
					}
				}
			}
			rowCounter++;
		}

		// Add the list of results to the reference set association
		if (this.parentObjOwnerAssociation)
			this.parentObj.setValue(context, this.template.getAssociation(), rowList);

		return true;
	}

	/**
	 * Create the IOQLTextGetRequest from Mendix and execute this.
	 * 
	 * @param context
	 *            The context to execute the query
	 * @return IDataTable with the results
	 * @throws CoreException
	 */
	private IDataTable executeOQL(IContext context) throws CoreException {
		IOQLTextGetRequest request = Core.createOQLTextGetRequest();
		request.setQuery(this.OQLQuery);
		log.debug("Execute OQL query: " + this.OQLQuery);
		return Core.retrieveOQLDataTable(context, request);
	}
}
