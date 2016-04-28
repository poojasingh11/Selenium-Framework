package com.kronos.telestaff.autotest.xml.reader;

import static com.kronos.telestaff.autotest.xml.reader.XMLDataParser.CONTAINER_NODE_NAME;
import static com.kronos.telestaff.autotest.xml.reader.XMLDataParser.FIELD_NODE_NAME;
import static com.kronos.telestaff.autotest.xml.reader.XMLDataParser.LIST_NODE_NAME;
import static com.kronos.telestaff.autotest.xml.reader.XMLDataParser.NAME_ATTRIBUTE_NAME;
import static com.kronos.telestaff.autotest.xml.reader.XMLDataParser.VALUE_ATTRIBUTE_NAME;
import static com.kronos.telestaff.autotest.xml.reader.XMLDataParser.TYPE_ATTRIBUTE_NAME;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@SuppressWarnings("deprecation")
public class XMLDataWriter {
	

	public String writeXML( IntDataContainer dataContainer )
	{
		Document document = createXMLDocument( dataContainer );
		OutputFormat format = new OutputFormat( document );
		StringWriter stringOut = new StringWriter();
		XMLSerializer serial = new XMLSerializer( stringOut, format );
		try
		{
			serial.serialize( document );
		}
		catch ( IOException ioex )
		{
			throw new RuntimeException( ioex );
		}
		return stringOut.toString();
	}
	
	public void writeXML(IntDataContainer dataContainer, String filePath)
	{
		Document document = createXMLDocument( dataContainer );
		OutputFormat format = new OutputFormat( document );
		try
		{
			FileWriter fileOut = new FileWriter(filePath );
			XMLSerializer serial = new XMLSerializer( fileOut, format );
			serial.serialize( document );
		}
		catch ( IOException ioex )
		{
			throw new RuntimeException( ioex );
		}
	}

	public Document createXMLDocument( IntDataContainer dataContainer )
	{
		Document document = createNewDocument();

		// add the root container to the document, which will recursively add
		// all other containers
		Element rootElement = createContainerElement( document, dataContainer );
		document.appendChild( rootElement );
		return document;
	}

	public Element createContainerElement( Document document, IntDataContainer dataContainer )
	{
		// create the element for this container
		Element containerElement = document.createElement( CONTAINER_NODE_NAME );
		containerElement.setAttribute( NAME_ATTRIBUTE_NAME, dataContainer.getName() );

		for ( IntDataContainer childContainer : dataContainer.getContainers().values() )
		{
			// Call back to this function to recursively create the container
			// elements
			Element childContainerElement = createContainerElement( document, childContainer );
			// add the child container to its parent container
			containerElement.appendChild( childContainerElement );
		}

		for ( IntContainerField field : dataContainer.getFields() )
		{
			// create the document element
			Element fieldElement = document.createElement( FIELD_NODE_NAME );
			// set the name and value attributes
			fieldElement.setAttribute( NAME_ATTRIBUTE_NAME, field.getName() );
			fieldElement.setAttribute( VALUE_ATTRIBUTE_NAME, field.getValue() );
			fieldElement.setAttribute( TYPE_ATTRIBUTE_NAME, field.getType() );

			// Add the new field element to its parent container
			containerElement.appendChild( fieldElement );
		}

		for ( IntContainerList childList : dataContainer.getLists().values() )
		{
			// Create the list element, which will recursively add all child
			// elements
			Element listElement = createListElement( document, childList );
			// Add the new list element to its parent container
			containerElement.appendChild( listElement );			
		}

		return containerElement;
	}

	public Element createListElement( Document document, IntContainerList list )
	{
		// Create the new list element and set its attributes
		Element listElement = document.createElement( LIST_NODE_NAME );
		listElement.setAttribute( NAME_ATTRIBUTE_NAME, list.getName() );

		// For each container in the list, recursively create the container
		// elements, and add
		// them to the list
		for ( IntDataContainer container : list.getDataContainers() )
		{
			Element containerElement = createContainerElement( document, container );
			listElement.appendChild( containerElement );
		}
		return listElement;
	}

	public Document createNewDocument()
	{
		try
		{
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			Document document = domBuilder.newDocument();
			return document;
		}
		catch ( ParserConfigurationException pex )
		{
			throw new RuntimeException( pex );
		}
	}
}
