package com.kronos.telestaff.autotest.staf;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

import org.junit.Assert;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

import com.kronos.telestaff.autotest.xml.reader.*;

public class AutoPopulate extends PageObject  {

	
	private int repeat = 0;

	
	public void populateValues(Object page, List<IntContainerField> xmlFields) {

		System.out.println("Populating Values on the Screen: "
				+ page.getClass().getSimpleName().toUpperCase());
		System.out
				.println("------------------------------------------------------");
		Map<String, WebElementFacade> elements = getPageElements(page);
		Map<String, Field> pageFields = getPageFields(page);
		for (IntContainerField xmlField : xmlFields) {
			if (pageFields.containsKey(xmlField.getName())) {
				String findByValue = null;
				WebElementFacade element = elements.get(xmlField.getName());
				String elementValue = xmlField.getValue();
				String prefix = validateElementPrefix(xmlField.getName());
				populateValueIntoElement(findByValue, prefix,xmlField.getName(), element, elementValue);

			}
		}
	}
	
	private String validateElementPrefix(String elementName) {
		String prefix = elementName.split("\\_")[0];
		if (prefix != null
				&& (prefix.equalsIgnoreCase("tbx")
						|| prefix.equalsIgnoreCase("btn")
						|| prefix.equalsIgnoreCase("lbl")
						|| prefix.equalsIgnoreCase("lnk")
						|| prefix.equalsIgnoreCase("ddl")
						|| prefix.equalsIgnoreCase("cbx")
						|| prefix.equalsIgnoreCase("img") || prefix
							.equalsIgnoreCase("rbn"))) {

		} else {
			Assert.fail("The element name '"
					+ elementName
					+ "' does not have a proper prefix; Allowed prefixes are : tbx, btn, lbl, lnk, ddl, cbx, img, rbn");
		}
		return prefix;
		
	}
	
	private void populateValueIntoElement(String findByValue, String prefix,
			String fieldName, WebElementFacade element, String elementValue) {
		try {
			element.waitUntilVisible();
			if (elementValue.contains("wait-")) {
				String waitInfo = elementValue.split(":")[0];
				elementValue = elementValue.split(":")[1];
				int waitTime = Integer.parseInt(waitInfo.split("-")[1]);
				System.out.println("Waiting for " + waitTime + " seconds...");
				waitABit(waitTime * 1000);
			}
			if (prefix.equals("tbx")) {
				element.type(elementValue);
				System.out.println("Populated '" + elementValue
						+ "' in to TextBox ( '" + fieldName + " -> "
						+ findByValue + "' )");
			} else if (prefix.equals("btn") || prefix.equals("lnk")
					|| prefix.equals("img")) {
				element.click();
				System.out.println("Clicked on ( '" + fieldName + " -> "
						+ findByValue + "' )");
			} else if (prefix.equals("ddl")) {
				if (elementValue.contains("Index:")) {
					if (element.getSelectOptions().size() > 0) {
						element.selectByIndex(Integer.parseInt(elementValue
							.split(":")[1]));
					} else {
						Assert.fail("Element " + fieldName + " does not have any options loaded");
					}
				} else {
					if (element.getSelectOptions().contains(elementValue)) {
						element.selectByVisibleText(elementValue);
					} else {
						Assert.fail("Element " + fieldName + " does not have the option " + elementValue);
					}
				}
				System.out.println("Selected '" + elementValue
						+ "' in the dropdown ( '" + fieldName + " -> "
						+ findByValue + "' )");
			} else if (prefix.equals("cbx") || prefix.equals("rbn")) {
				if (elementValue.equals("ON")) {
					if (!element.isSelected()) {
						element.click();
					}
				} else {
					if (element.isSelected()) {
						element.click();
					}
				}
				System.out.println("Turned '" + elementValue
						+ "' the checkbox ( '" + fieldName + " -> "
						+ findByValue + "' )");
			}
			repeat = 0;
		} catch (StaleElementReferenceException ex) {
			if (repeat < 2) {
				repeat++;
				populateValueIntoElement(findByValue, prefix, fieldName,
						element, elementValue);
			} else {
				repeat = 0;
				Assert.fail("StaleElementReferenceException thrown on element "
						+ fieldName + " while setting its value to "
						+ elementValue + ". Hence failing the test");
			}
		} catch (NoSuchElementException ex) {
			if (repeat < 2) {
				repeat++;
				populateValueIntoElement(findByValue, prefix, fieldName,
						element, elementValue);
			} else {
				repeat = 0;
				Assert.fail("NoSuchElementException thrown on element "
						+ fieldName + " while setting its value to "
						+ elementValue + ". Hence failing the test");
			}
		} catch (ElementNotVisibleException ex) {
			if (repeat < 2) {
				repeat++;
				populateValueIntoElement(findByValue, prefix, fieldName,
						element, elementValue);
			} else {
				repeat = 0;
				Assert.fail("ElementNotVisibleException thrown on element "
						+ fieldName + " while setting its value to "
						+ elementValue + ". Hence failing the test");
			}
		}
	}
	
	public Map<String, Field> getPageFields(Object page) {

		LinkedHashMap<String, Field> ScreenElementMap = new LinkedHashMap<String, Field>();

		Field[] fields = page.getClass().getFields();

		for (Field f : fields) {

			if (f.getAnnotation(FindBy.class) != null) {

				try {

					ScreenElementMap.put(f.getName(), f);

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}

		return ScreenElementMap;
	}

	
	
	
	
	public Map<String, WebElementFacade> getPageElements(Object page) {

		LinkedHashMap<String, WebElementFacade> elements = new LinkedHashMap<String, WebElementFacade>();

		Field[] fields = page.getClass().getFields();

		for (Field f : fields) {

			if (f.getAnnotation(FindBy.class) != null) {

				try {

					elements.put(f.getName(), (WebElementFacade) f.get(page));

				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		return elements;
	}

}
	
	
