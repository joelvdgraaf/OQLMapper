# OQLMapper
A technical Mendix module to integrate OQL queries within your microflows by easily calling 1 Java action and get the data in youw (non) persistent entities.

OQL in Mendix is a powerful language to retrieve data from your database based your own developed queries. In the Mendix Platform is OQL only used in reporting, which is a pitty. This module will let you use OQL within your microflows based on a simple Java action and the MxModelReflection module.

# Features
- Execute OQL query from java action and use the created objects in a MF
- Use a context object and replace a token (eg. $Object) by the Object/ID
- Import / Export templates to XML
- Import XML from Microflow on startup (useful for production environments)

# Configuration
In the module you configure mapping templates. A template has the OQL query as text saved, which means the module will not do any checks on that query. The advice is to create the query in a DataSet in the modeler and copy the result to the module. A template needs a parent object, reference and row object.
- Parent object type: The object type that will given to the Java action to attach each row of the result.
- Association: The association between the parent object and the row object.
- Row Object type: The object that will be created for each row in the result and will be attached to parent object based on the reference.

Makesure that the object that are used are non-persistent object. Persistent objects can be used, but won't be committed to the database!

The next step for the template is the mapping: a simple structure to parse each column to an attribute of the row object.
- Alias name: The name of the column that is specified in the OQL query.
- Parser type: The way how the data must be parsed to store it. There aren't any checks if the parser is correct related to the attribute
- Attribute mapping: The attribute of the row object to store the data from the column.

#Execution
All templates are stored in the Java memory after startup and on the before commit of the template. This advantage will give the ability to simple call the Java action with the unique name of the template and the parent object and the OQL query will be executed. It isn't needed to retrieve the template first. To execute the OQL query call the Java action 'ExecuteOQLQuery'. There is an option to give another OQL query than there is configured.

The Java action will not handle any exceptions, it expects the microflow to handle the exceptions properly. The exceptions that can be thrown are from the Mendix Platform based on incorrect OQL queries or incorrect data parsing. The Java action it self can also throw exception, for example:
- The requested template based on the name can't be found.
- The parent object isn't given to the Java action.
- The given parent object isn't equal or a specialization of the the configured parent object.
