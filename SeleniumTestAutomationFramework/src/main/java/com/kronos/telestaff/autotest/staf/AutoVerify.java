package com.kronos.telestaff.autotest.staf;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.serenitybdd.core.pages.WebElementFacade;

import org.junit.Assert;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import com.kronos.telestaff.autotest.xml.reader.*;


public class AutoVerify {
	private int repeat = 0;
	AutoPopulate autoPopulate = new AutoPopulate();
	public void verifyValues(Object page, List<IntContainerField> xmlFields) {

		System.out.println("Verifying Values on the Screen: " + page.getClass().getSimpleName().toUpperCase());
		System.out.println("------------------------------------------------------");
		Map<String, WebElementFacade> elements = autoPopulate.getPageElements(page);
		Map<String, Field> pageFields = autoPopulate.getPageFields(page);
		for (IntContainerField xmlField : xmlFields) {
			if (pageFields.containsKey(xmlField.getName())) {
				// Field field = pageFields.get(xmlField.getName());
				String findByValue = null;
				
				WebElementFacade element = elements.get(xmlField.getName());
				String elementValue = xmlField.getValue();
				String prefix = validateElementPrefix(xmlField.getName());
				verifyValueInElement(findByValue, prefix, xmlField.getName(), element, elementValue);

			}
			/*
			 * else { Assert.fail("FAILURE : (" + xmlField.getName() +
			 * "'s)Value is Present in DataSet but Element Locator is not defined in Screen Class."
			 * ); }
			 */
		}
	}
	
	private String validateElementPrefix(String elementName) {
		String prefix = elementName.split("\\_")[0];
		if (prefix != null) {

		} else {
			Assert.fail("The element name '" + elementName + "' does not have a prefix; prefixes is null");
		}
		return prefix;
	}
	private void verifyValueInElement(String findByValue, String prefix, String fieldName, WebElementFacade element,
			String elementValue) {
		try {
			element.waitUntilPresent();
			if (prefix.equals("lbl")) {
				element.shouldContainOnlyText(elementValue);
				System.out.println("Label '" + fieldName + "' contains the expected value ( '" + elementValue + "' )");
			} else if (prefix.equals("tbx")) {
				if (element.getText().trim().equals(elementValue)
						|| element.getAttribute("value").trim().equals(elementValue)) {
					System.out.println(
							"TextBox '" + fieldName + "' contains the expected value ( '" + elementValue + "' )");
				} else {
					System.out.println("TextBox '" + fieldName + "' does not contain the expected value ( '"
							+ elementValue + "' );Actual Value: " + element.getAttribute("value"));
					return;
				}
			} else if (prefix.equals("ddl")) {
				element.shouldContainSelectedOption(elementValue);
				System.out.println(
						"DropDownList '" + fieldName + "' contains the expected value ( '" + elementValue + "' )");
			} else if (prefix.equals("btn") || prefix.equals("cbx") || prefix.equals("lnk")) {
				return;
			}
			repeat = 0;
		} catch (StaleElementReferenceException ex) {
			if (repeat < 2) {
				verifyValueInElement(findByValue, prefix, fieldName, element, elementValue);
				repeat++;
			} else {
				throw ex;
			}
		} catch (NoSuchElementException ex) {
			if (repeat < 2) {
				verifyValueInElement(findByValue, prefix, fieldName, element, elementValue);
				repeat++;
			} else {
				throw ex;
			}
		} catch (ElementNotVisibleException ex) {
			if (repeat < 2) {
				verifyValueInElement(findByValue, prefix, fieldName, element, elementValue);
				repeat++;
			} else {
				throw ex;
			}
		}
	}
	
	
	
}
