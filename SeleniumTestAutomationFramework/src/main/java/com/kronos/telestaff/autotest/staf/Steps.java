package com.kronos.telestaff.autotest.staf;
import java.net.URL;

import com.kronos.telestaff.autotest.xml.reader.IntDataContainer;
import com.kronos.telestaff.autotest.xml.reader.XMLDataParser;

import net.thucydides.core.steps.ScenarioSteps;

@SuppressWarnings("serial")
public class Steps extends ScenarioSteps  {
	protected static Environment currentEnvironment;
	protected static IntDataContainer data;
	protected static IntDataContainer envData;
	protected static IntDataContainer commonData;
	private static STAFController controller;
	public static boolean isInitialized = false;
	
	public void initialize() {
		if (!isInitialized) {
			initEnv();
			initData("data.xml");
		}
	}
	
	private void initEnv() {
		controller = STAFController.getInstance();

		if (!controller.isInitialized()) {
			throw new IllegalStateException("STAF Controller is not initialized. Please contact Framework Administrator.");
		}
		currentEnvironment = controller.getEnvironment();
	}
	
	private void initData(String xmlName) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(xmlName);
		data = new XMLDataParser().parseXML(url.toString());
		envData = data.getContainer("Environments").getContainer(currentEnvironment.getKey());
		commonData = data.getContainer("CommonData");
		isInitialized = true;
	}

}
