package com.kronos.telestaff.autotest.xml.reader;

import java.util.ArrayList;

import java.util.List;

public class ContainerList implements IntContainerList {
	/**
	 * The containers that make up this data list
	 */
	public List<IntDataContainer> containers = new ArrayList<IntDataContainer>();

	/**
	 * The name of this data list, which is used as its key by its child
	 * container.
	 */
	private String name;

	private DataContainer parentContainer = null;
	
	/**
	 * Creates a new XMLContainerList.
	 * 
	 * @param name
	 *            All DataLists must specify a name.
	 */
	public ContainerList(String name)
	{
		this.name = name;
	}


	/* (non-Javadoc)
	 * @see IContainerList#getDataContainers()
	 */
	public List<IntDataContainer> getDataContainers()
	{
		return containers;
	}

	/* (non-Javadoc)
	 * @see IContainerList#getContainer(int)
	 */
	public IntDataContainer getContainer(int containerIndex)
	{
		return containers.get(containerIndex);
	}

	/**
	 * This method was a duplicate of addContainer.
	 * Please use the addContainer method instead.
	 */
	@Deprecated
	public void addDataContainer(IntDataContainer xMLDataContainer)
	{
		containers.add(xMLDataContainer);
	}

	/**
	 * Gets the name of this list.
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	public int size()
	{
		return containers.size();
	}

	
	public void addContainer( IntDataContainer dataContainer )
	{
		containers.add( dataContainer );
		((DataContainer)dataContainer).setParent(this);
	}
	
	/**
	 * Gets the container that contains this list.
	 * 
	 * This is set by the parent container when the list is added to the container.
	 * 
	 * @return
	 */
	protected DataContainer getParentContainer()
	{
		return parentContainer;
	}
	
	/**
	 * Gets the container that contains this list.
	 * 
	 * This is set by the parent container when the list is added to the container.
	 * 
	 * @param parentContainer
	 */
	protected void setParentContainer(DataContainer parentContainer)
	{
		this.parentContainer = parentContainer;
	}
	
	/**
	 * Walks back up the container chain to the root container that is the ultimate
	 * parent of this list.
	 * @return
	 */protected DataContainer getRootContainer()
	{
		return (DataContainer)parentContainer.getRootContainer();
	}
	
	/**
	 * Loops over the containers in the list, and tells each container to resolve
	 * all field links among its children. 
	 */
	protected void resolveAllLinks()
	{
		for (IntDataContainer iContainer : containers)
		{
			DataContainer container = (DataContainer)iContainer;
			container.resolveAllLinks();
		}
	}
	
}
