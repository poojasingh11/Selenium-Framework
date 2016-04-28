package com.kronos.telestaff.autotest.staf;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DriverSetup {
	
	public void setup() {
		if (System.getProperty("webdriver.driver") != null) {
			if (System.getProperty("webdriver.driver").equals("chrome")) {
				String pathDriver = System.getProperty("user.home") + "\\chromedriver.exe";
				System.setProperty("webdriver.chrome.driver", pathDriver);
				copyDriver("/chromedriver.exe", "\\chromedriver.exe");
			} else if (System.getProperty("webdriver.driver").equals("iexplorer")) {
				String pathDriver = System.getProperty("user.home") + "\\IEDriverServer.exe";
				System.setProperty("webdriver.ie.driver", pathDriver);
				copyDriver("/IEDriverServer.exe", "\\IEDriverServer.exe");
			}
		} else {
			String pathDriver = System.getProperty("user.home") + "\\chromedriver.exe";
			System.setProperty("webdriver.chrome.driver", pathDriver);
			copyDriver("/chromedriver.exe", "\\chromedriver.exe");
			pathDriver = System.getProperty("user.home") + "\\IEDriverServer.exe";
			System.setProperty("webdriver.ie.driver", pathDriver);
			copyDriver("/IEDriverServer.exe", "\\IEDriverServer.exe");
		}
	}
	
	public void copyDriver(String resourceLocation, String driverLocation) {

		InputStream is = getClass().getResourceAsStream(resourceLocation);
		OutputStream os = null;
		try {
			os = new FileOutputStream(System.getProperty("user.home") + driverLocation);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] buffer = new byte[4096];
		int length;
		try {
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
