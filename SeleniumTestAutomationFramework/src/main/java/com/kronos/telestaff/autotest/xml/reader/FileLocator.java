package com.kronos.telestaff.autotest.xml.reader;

import java.io.File;
import java.net.URL;

public class FileLocator {
	public static String validateFilePath(String filePath)
	{
		
		if (fileExists(filePath) == true)
		{
			return filePath;
		}
		else
		{
			return getFilePathAsResource(filePath);
		}
	}
	
	public static boolean fileExists(String filePath)
	{
		File file = new File(filePath);
		return file.exists();
	}
	
	public static String getFilePathAsResource(String filePath)
	{
		URL url = getResource(filePath);
		if (url == null)
		{
			return filePath;
		}
		else
		{
			return url.getPath();
		}
	}
	
	public static URL getResource(String filePath)
	{
		URL url = FileLocator.class.getResource(filePath);
		return url;
	}



}
