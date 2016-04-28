package com.kronos.telestaff.autotest.xml.reader;

import java.util.List;
import java.util.Map;

/**
 * Interface that defines how a data container should be implemented.
 * 
 * Data containers represent hierarchical data structures (such as XML), and are
 * made up of containers, lists, and fields.
 * 
 * Containers can contain other containers, lists, or fields.
 * 
 * Lists can only contain containers.
 * 
 * Fields are leaf nodes, and include a name and a value.
 * 
 */
public interface IntDataContainer {


	/**
	 * Gets a field value by name from this container The name can either be a field
	 * name, or a path through containers to a field using dot-notation e.g.
	 * "container1.container2.field"
	 *
	 * Although this approach will work for most field and container names with dots in them, it is not
	 * recommended to use dots in container names and field names, as it will make
	 * managing data files more confusing.
	 * 
	 * @param name - name of the field, or a path using dot-notation
	 * @return - value of the field, or null, if the field does not exist
	 */
	public String getFieldValue( String name );
	
	/**
	 * Gets a field by name from this container The name can either be a field
	 * name, or a path through containers to a field using dot-notation e.g.
	 * "container1.container2.field"
	 *
	 * Although this approach will work for most field and container names with dots in them, it is not
	 * recommended to use dots in container names and field names, as it will make
	 * managing data files more confusing.
	 * 
	 * @param name - name of the field, or a path using dot-notation
	 * @return - IntContainerField object, or null, if the field does not exist
	 */
	public IntContainerField getField( String name );

	/**
	 * Gets a child container by name from this container
	 * 
	 * @param name -
	 *            name of the container
	 * @return - reference to the container, or null, if the container does not
	 *         exist
	 */
	public IntDataContainer getContainer( String name );

	/**
	 * Gets a child list by name from this container
	 * 
	 * @param name -
	 *            name of the list in this container
	 * 
	 * @return - reference to the list in this container, or null, if the list
	 *         does not exist
	 */
	public IntContainerList getList( String name );

	/**
	 * Gets the name of this container
	 * 
	 * @return - name of this container
	 */
	public String getName();

	/**
	 * Sets a field value for this container. Existing fields with the same name
	 * will be replaced with the new value.
	 * 
	 * @param name -
	 *            name of the field
	 * @param value -
	 *            value of the field
	 */
	public void setField( String name, String value, String type );

	/**
	 * Sets a field for this container. Existing fields with same name
	 * will be overwritten with the new field.
	 * 
	 * @param field -
	 *            IntContainerField object
	 */
	public void setField( IntContainerField field );

	/**
	 * Sets a field value for this container. 
	 * 
	 * @param name -
	 *            name of the field
	 * @param value -
	 *            value of the field
	 */
	public void setFieldValue( String name, String value );

	/**
	 * Adds a child container to this data container
	 * 
	 * Existing children with the same name as the container specified will be
	 * replaced by the new container
	 * 
	 * @param xMLDataContainer -
	 *            reference to child container that is added.
	 */
	public void addDataContainer( IntDataContainer dataContainer );

	/**
	 * Adds a child list to this data container
	 * 
	 * Existing lists with the same name as the new list will be replaced by the
	 * new list.
	 * 
	 * @param containerList -
	 *            list of containers or fields or both included
	 */
	public void addList( IntContainerList containerList );

	/**
	 * method to return all fields in this data container
	 * 
	 * 
	 * @return - reference to the map of fields (key value pairs)
	 */
	public List<IntContainerField> getFields();

	/**
	 * method to return mandatory fields in this data container
	 * 
	 * 
	 * @return - reference to the map of fields (key value pairs)
	 */
	public List<IntContainerField> getMandatoryFields();

	/**
	 * Returns all of the child containers of this container
	 */
	public Map<String, IntDataContainer> getContainers();

	/**
	 * Returns all of the child lists of this container
	 */
	public Map<String, IntContainerList> getLists();

	public void removeField(String fieldName);
	
	public List<IntContainerField> getMandatoryFieldsFromAllContainers();
	
	public List<IntContainerField> getFieldsFromAllContainers();
	
	public void removeFields(String... fieldNames);
	
	public void removeFieldsFromAllContainers(String... fieldNames);
	
	public void setActualValues();
	
	public void setActualValuesForAllContainers();
}
