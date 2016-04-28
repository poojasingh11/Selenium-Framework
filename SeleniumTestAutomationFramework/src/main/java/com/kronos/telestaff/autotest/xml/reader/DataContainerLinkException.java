package com.kronos.telestaff.autotest.xml.reader;

public class DataContainerLinkException extends RuntimeException {
private static final long serialVersionUID = 1L;
	
	private String containerName;
	private String fieldName;
	private String linkValue;
	
	public DataContainerLinkException(String containerName, String fieldName, String linkValue)
	{
		this.containerName = containerName;
		this.fieldName = fieldName;
		this.linkValue = linkValue;
	}

	@Override
	public String getMessage()
	{
		String message = "Could not resolve link in DataContainer:\n\tContainer Name=" + containerName + "\n\tField Name=" + fieldName + "\n\tLink Value=" + linkValue;
		return message;
	}
	
}
