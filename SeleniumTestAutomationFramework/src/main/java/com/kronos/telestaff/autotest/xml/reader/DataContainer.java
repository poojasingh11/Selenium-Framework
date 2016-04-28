package com.kronos.telestaff.autotest.xml.reader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kronos.telestaff.autotest.staf.*;

public class DataContainer implements IntDataContainer {

	/**
	 * The name of this data container which is used to identify it within the
	 * scope of its parent container
	 * 
	 * This value should be unique within the parent container, but does not
	 * need to be unique within a list.
	 */
	private String name;

	/**
	 * The collection of name/value pairs that represent the data values of this
	 * container
	 */
	private Map<String, IntContainerField> fields = new LinkedHashMap<String, IntContainerField>();

	/**
	 * The collection of child containers within this XMLDataContainer, stored
	 * in a map using their names as keys.
	 */
	private Map<String, IntDataContainer> containers = new LinkedHashMap<String, IntDataContainer>();

	/**
	 * The collection of child lists within this XMLDataContainer, stored in a
	 * map using their names as keys.
	 */
	private Map<String, IntContainerList> lists = new LinkedHashMap<String, IntContainerList>();

	private Object parent = null;

	/**
	 * Creates a new DataContainer
	 * 
	 * @param name
	 *            A name that will uniquely identify this container.
	 */
	public DataContainer(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDataContainer#getFieldValue(java.lang.String)
	 */
	public String getFieldValue(String name) {
		IntContainerField fieldObject = getField(name);
		if (fieldObject == null) {
			return null;
		} else {
			return fieldObject.getValue();
		}
	}

	public IntContainerField getField(String name) {
		if (name.contains(".") == true) {
			FieldPath fieldPath = new FieldPath(name);
			IntContainerField fieldObject = getFieldByPath(fieldPath);
			return fieldObject;
		} else {
			IntContainerField fieldObject = fields.get(name);
			return fieldObject;
		}
	}

	/**
	 * Checks this container to see if it contains a child container with the
	 * given name.
	 * 
	 * @param containerName
	 * @return
	 */
	private boolean hasContainer(String containerName) {
		return getContainers().keySet().contains(containerName);
	}

	/**
	 * Checks this container to see if it contains a field with the given name
	 * 
	 * @param fieldName
	 * @return
	 */
	private boolean hasField(String fieldName) {
		boolean isFound = false;
		List<IntContainerField> fields = getFields();
		for (IntContainerField field : fields) {
			if (field.getName().equals(fieldName)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}

	/**
	 * Internal method that uses a fieldPath object (created from a dotted field
	 * path string) to dig through the container heirarchy to find a field.
	 * 
	 * @param fieldPath
	 *            A FieldPath, which is an object representation of a field path
	 *            string e.g. "ParentContainer.ChildContainer.FieldName"
	 * @return The container field object
	 * 
	 *         Since field names and container names may themselves include
	 *         dots, this method will attempt to parse field names and container
	 *         names as follows:
	 * 
	 *         Given a field path as listed above (container1.container2.field)
	 *
	 *         -Check for a container called container1. If it exists, drill
	 *         into it, and look for the next container in the path
	 * 
	 *         -If container1 doesn't exist, try appending the next section of
	 *         the path and look for that container "container1.conainer2" in
	 *         this case
	 * 
	 *         -Each time we get into a container, first try the rest of the
	 *         path as a field name, then start breaking it up again as
	 *         containers if the field doesn't exist.
	 */
	protected IntContainerField getFieldByPath(FieldPath fieldPath) {
		while (fieldPath.hasRemainingTokens() == true) {
			String currentToken = fieldPath.getNextPathSegment();
			if (hasContainer(currentToken) == true) {
				DataContainer nextContainer = (DataContainer) this
						.getContainer(currentToken);
				FieldPath remainingFieldPath = fieldPath.getRemainingPath();
				return nextContainer.getFieldByPath(remainingFieldPath);
			} else if (hasField(currentToken) == true) {
				return fields.get(currentToken);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDataContainer#getContainer(java.lang.String)
	 */
	public IntDataContainer getContainer(String name) {
		return containers.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDataContainer#getList(java.lang.String)
	 */
	public IntContainerList getList(String name) {
		return lists.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDataContainer#setField(java.lang.String, java.lang.String)
	 */
	public void setField(String name, String value, String type) {
		IntContainerField field = new ContainerField(name, value, type);
		fields.put(name, field);
		((ContainerField) field).setParent(this);
	}

	public void setField(IntContainerField field) {
		fields.remove(field.getName());
		fields.put(field.getName(), field);
		((ContainerField) field).setParent(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDataContainer#getFieldValue(java.lang.String)
	 */
	public void setFieldValue(String name, String value) {
		IntContainerField fieldObject = getField(name);
		if (fieldObject == null) {
			throw new RuntimeException("Unable to find field called " + name
					+ " to update its value");
		} else {
			fieldObject.setValue(value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDataContainer#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * Adds a child container to this data container
	 * 
	 * Existing children with the same name as the container specified will be
	 * replaced by the new container
	 */
	public void addDataContainer(IntDataContainer dataContainer) {
		containers.put(dataContainer.getName(), dataContainer);
		((DataContainer) dataContainer).setParent(this);
	}

	/**
	 * Sets the parent container (or list) of this container
	 * 
	 * This is set by the parent as this container is added to a parent object.
	 * 
	 * @param parentContainerOrList
	 */
	protected void setParent(Object parentContainerOrList) {
		this.parent = parentContainerOrList;
	}

	/**
	 * @return The container or list that is this containers parent.
	 * 
	 *         If this container doesn't have a parent, this value is null
	 */
	protected Object getParent() {
		return parent;
	}

	/**
	 * Drives back up through the container heirarchy to the root container that
	 * is the ultimate parent of this container.
	 * 
	 * If this container is the root container, it will return itself.
	 * 
	 * @return
	 */
	protected DataContainer getRootContainer() {
		if (parent == null) {
			return this;
		} else if (parent instanceof DataContainer) {
			return ((DataContainer) parent).getRootContainer();
		} else if (parent instanceof ContainerList) {
			return ((ContainerList) parent).getRootContainer();
		} else {
			throw new RuntimeException(
					"Could not find parent container for container '" + name
							+ "'");
		}
	}

	/**
	 * Adds a child list to this data container
	 * 
	 * Existing lists with the same name as the new list will be replaced by the
	 * new list.
	 */
	public void addList(IntContainerList containerList) {
		lists.put(containerList.getName(), containerList);
		((ContainerList) containerList).setParentContainer(this);
	}


	public List<IntContainerField> getMandatoryFields() {
		List<IntContainerField> containerFields = new ArrayList<IntContainerField>();
		for (String fieldName : fields.keySet()) {
			if (fields.get(fieldName).getType() != null
					&& fields.get(fieldName).getType().equals("mandatory")) {
				containerFields.add(fields.get(fieldName));
			}
		}
		return containerFields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDataContainer#getFields()
	 */
	public List<IntContainerField> getFields() {
		List<IntContainerField> containerFields = new ArrayList<IntContainerField>();
		for (String fieldName : fields.keySet()) {
			containerFields.add(fields.get(fieldName));
		}
		return containerFields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDataContainer#getContainers()
	 */
	public Map<String, IntDataContainer> getContainers() {
		return containers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDataContainer#getLists()
	 */
	public Map<String, IntContainerList> getLists() {
		return lists;
	}

	/**
	 * Recursively digs through all child fields, containers, and lists, looking
	 * for fields that are links to other fields in the heirarchy.
	 */
	protected void resolveAllLinks() {
		resolveFieldLinks();
		resolveContainerLinks();
		resolveListLinks();
	}

	/**
	 * Loops over all of the fields in this container, and resolves links to
	 * other fields in the container heirarchy.
	 */
	private void resolveFieldLinks() {
		for (IntContainerField iField : fields.values()) {
			ContainerField field = (ContainerField) iField;
			field.resolveLink();
		}
	}

	/**
	 * Loops over each child container in this container, and tells it
	 * recursively resolve all field links to other fields in the container
	 * heirarchy
	 */
	private void resolveContainerLinks() {
		for (IntDataContainer iChildContainer : containers.values()) {
			DataContainer childContainer = (DataContainer) iChildContainer;
			childContainer.resolveAllLinks();
		}
	}

	/**
	 * Loops over each list in this container, tells it to recursively resolve
	 * all links to other fields in the container heirarchy.
	 */
	private void resolveListLinks() {
		for (IntContainerList iChildList : lists.values()) {
			ContainerList childList = (ContainerList) iChildList;
			childList.resolveAllLinks();
		}
	}

	/**
	 * Sets the name of this container. This is used to set names for custom
	 * populated containers.
	 * 
	 * @param name
	 */
	protected void setName(String name) {
		this.name = name;
	}

	/**
	 * Inner class used to represent dotted field paths (e.g.
	 * "ParentContainer.ChildContainer.FieldName")
	 * 
	 * See the IDataContainer.getField() method for more details on how this is
	 * used.
	 */
	protected class FieldPath {
		// All the element names split around the dots
		String[] tokens = null;
		// Cursor representing which token is currently being checked
		int tokenCursor = 0;

		/**
		 * Creates a field path object from a dotted field path string: e.g.
		 * "ParentContainer.ChildContainer.FieldName"
		 * 
		 * @param dottedFieldPath
		 */
		private FieldPath(String dottedFieldPath) {
			tokens = dottedFieldPath.split("\\.");
		}

		/**
		 * Breaks off the next part of the fieldpath that needs to be checked.
		 * 
		 * This is used to check for container names or field names that include
		 * dots in the names.
		 * 
		 * @return
		 */
		private String getNextPathSegment() {
			String nextToken = "";
			for (int i = 0; i <= tokenCursor; i++) {
				nextToken = nextToken + tokens[i];
				if (i < tokenCursor) {
					nextToken = nextToken + ".";
				}
			}
			tokenCursor = tokenCursor + 1;
			return nextToken;
		}

		/**
		 * Starting from the cursor, gets/rebuilds the rest of the pathString,
		 * as each token may be a container or field name, or it may be part of
		 * a dotted container or field name.
		 * 
		 * @return
		 */
		private FieldPath getRemainingPath() {
			String remainingToken = "";
			for (int i = tokenCursor; i < tokens.length; i++) {
				remainingToken = remainingToken + tokens[i];
				if (i < tokens.length - 1) {
					remainingToken = remainingToken + ".";
				}
			}
			FieldPath newFieldPath = new FieldPath(remainingToken);
			return newFieldPath;
		}

		/**
		 * Checks whether we're on the last token of the path
		 * 
		 * @return
		 */
		private boolean hasRemainingTokens() {
			if (tokenCursor < tokens.length) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public void removeField(String fieldName) {
		List<IntContainerField> containerFields = getFields();
		for (IntContainerField field : containerFields) {
			if (field.getName().equals(fieldName)) {
				containerFields.remove(field);
				break;
			}
		}
		fields = new LinkedHashMap<String, IntContainerField>();
		for (IntContainerField field : containerFields) {
			fields.put(field.getName(), field);
		}
	}

	@Override
	public List<IntContainerField> getMandatoryFieldsFromAllContainers() {
		List<IntContainerField> mandatoryFields = new ArrayList<IntContainerField>();
		mandatoryFields = getMandatoryFields();
		Map<String, IntDataContainer> subContainers = getContainers();
		for (String container : subContainers.keySet()) {
			List<IntContainerField> fields = subContainers.get(container)
					.getMandatoryFields();
			for (IntContainerField field : fields) {
				mandatoryFields.add(field);
			}
		}
		return mandatoryFields;
	}

	@Override
	public List<IntContainerField> getFieldsFromAllContainers() {
		List<IntContainerField> allFields = new ArrayList<IntContainerField>();
		allFields = getFields();
		Map<String, IntDataContainer> subContainers = getContainers();
		for (String container : subContainers.keySet()) {
			List<IntContainerField> fields = subContainers.get(container)
					.getFields();
			for (IntContainerField field : fields) {
				allFields.add(field);
			}
		}
		return allFields;
	}

	@Override
	public void removeFields(String... fieldNames) {
		for (String fieldName : fieldNames) {
			removeField(fieldName);
		}
	}

	@Override
	public void removeFieldsFromAllContainers(String... fieldNames) {
		for (String fieldName : fieldNames) {
			removeField(fieldName);
			Map<String, IntDataContainer> subContainers = getContainers();
			for (String container : subContainers.keySet()) {
				subContainers.get(container).removeField(fieldName);
			}
		}
	}

	public static String get_value_with_date_time(String value) {
		DateFormat dateFormat = new SimpleDateFormat("MMdd_HHmmss");
		Date date = new Date();
		return value + "_" + dateFormat.format(date);
	}

	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}

	@Override
	public void setActualValues() {
		List<IntContainerField> fields = getFields();
		for (IntContainerField field : fields) {
			if (field.getValue().contains("RefreshData:")) {
				field.setValue(Page.envData.getContainer("RefreshData").getFieldValue(
								field.getValue().split(":")[1]));
			}
			if (field.getValue().contains("DateTime:")) {
				field.setValue(get_value_with_date_time(field
						.getValue().split(":")[1]));
			}
			if (field.getValue().contains("Date:")) {
				field.setValue(getDate());
			}
			if (field.getValue().contains("->")) {
				String value = null;
				String containerName = field.getValue().split("->")[0];
				String fieldName = field.getValue().split("->")[1];
				if (Page.commonData.getContainer(containerName) != null) {
					value = Page.commonData.getContainer(containerName)
							.getFieldValue(fieldName);
				} else {
					value = Page.envData.getContainer(containerName)
							.getFieldValue(fieldName);
				}
				field.setValue(value);
			}
		}
	}

	@Override
	public void setActualValuesForAllContainers() {
		setActualValues();
		for (String containerName : getContainers().keySet()) {
			getContainer(containerName).setActualValues();
		}
	}

}
