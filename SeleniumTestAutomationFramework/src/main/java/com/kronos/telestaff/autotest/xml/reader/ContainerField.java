package com.kronos.telestaff.autotest.xml.reader;

public class ContainerField implements IntContainerField {
	private String name;
	private String value;
	private String link;
	private String type;
	private DataContainer parentContainer=null;
	/**
	 * Creates a container with the given name and value
	 * 
	 * @param name
	 * @param value
	 */
	public ContainerField(String name, String value, String type)
	{
		this.name = name;
		this.value = value;
		this.type = type;
	}
	/**
	 * Creates a container field with the given name.
	 * 
	 * Use this constructor when creating link fields
	 * 
	 * @param name
	 */
	public ContainerField(String name)
	{
		this.name = name;		
	}

	public String getName()
	{
		return name;
	}

	
	public String getValue()
	{
		return value;
	}	

	
	public String getType()
	{
		return type;
	}	
	
	/**
	 * Returns the string value that is the path to the field this field links to.
	 * 
	 * @return
	 */
	public String getLink()
	{
		return link;	
	}
	
	/**
	 * Sets the path to the field that this field links to.
	 * @param link
	 */
	public void setLink(String link)
	{
		this.link = link;
	}
	
	/**
	 * Checks whether this field is a link to another field in the container
	 * @return
	 */
	public boolean isLink()
	{
		if (link != null && link.length() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Tells this field who its parent container is.
	 * 
	 * The container will set this when this field is added to the container.
	 * @param parentContainer
	 */
	protected void setParent(DataContainer parentContainer)
	{
		this.parentContainer = parentContainer;		
	}
	
	/**
	 * Gets the parent container that contains this field
	 * @return
	 */
	protected DataContainer getParent()
	{
		return parentContainer;	
	}
	
	/**
	 * Walks back up the container chain to the root container that is the ultimate
	 * parent of this field.
	 * @return
	 */
	protected DataContainer getRootContainer()
	{
		return (DataContainer)parentContainer.getRootContainer();
	}
	
	/**
	 * Attempts to set the value of this field by using the path in the link member
	 * to find  
	 */
	protected void resolveLink()
	{
		if (link == null)
		{
			return;
		}
		
		DataContainer rootContainer = getRootContainer();
		IntContainerField fieldObject = rootContainer.getField(link);		
		if (fieldObject == null)
		{			
			//Can't find the field
			throw new DataContainerLinkException(parentContainer.getName(), name, link);
		}
		else
		{			
			if (fieldObject.getValue() == null)
			{
				//Try to resolve the field as a link, just in case...
				((ContainerField)fieldObject).resolveLink();
			}
			this.value = fieldObject.getValue();
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setType(String type) {
		this.type = type;
	}


}
