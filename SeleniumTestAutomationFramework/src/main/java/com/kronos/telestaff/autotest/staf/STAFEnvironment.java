package com.kronos.telestaff.autotest.staf;
import java.lang.reflect.AnnotatedElement;

public class STAFEnvironment {
	
	public static void registerEnvironment(Class<?> klass) {
		Environment environment = null;

		AnnotatedElement element = klass;
		environment = getEnvironment(element);

		STAFController controller = STAFController.getInstance();
		controller.init(environment);
		
		DriverSetup driver = new DriverSetup();
		driver.setup();
	}

	private static Environment getEnvironment(AnnotatedElement element) {
		Environment tempEnvironment;

		if (System.getProperty("staf.environment.key") == null) {
			TestEnvironment envAnnotation = element.getAnnotation(TestEnvironment.class);
			tempEnvironment = envAnnotation.value();
			System.setProperty("staf.environment.key", tempEnvironment.getKey());
		} else {
			tempEnvironment = Environment.fromString(System.getProperty("staf.environment.key"));
		}

		return tempEnvironment;
	}

}
