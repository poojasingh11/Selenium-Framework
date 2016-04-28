package com.kronos.telestaff.autotest.xml.reader;

import java.net.URL;

public class XMLContainerBuilder {

	
	public IntDataContainer buildContainer( String args )
	{
		XMLDataParser parser = new XMLDataParser();
		URL url = getClass().getResource(args);
		IntDataContainer container = parser.parseXML(url.toString());		
		return container;
	}
}
