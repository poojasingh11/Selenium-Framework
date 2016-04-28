package com.kronos.telestaff.autotest.staf;

public class STAFController {

	private static STAFController instance = null;
	private Environment environment;
	private Boolean initialized = false;
	
private STAFController() {
		
	}
	
	public static STAFController getInstance() {
		if (instance == null) {
			synchronized (STAFController.class) {
				if (instance == null) {
					instance = new STAFController();
				}
			}
		}
		return instance;
	}
	
	public void init(Environment env) {
		if (this.environment == null) {
			this.environment = env;
			this.initialized = true;
		}
	}
	
	public void setEnvironment(Environment env) {
		this.environment = env;
	}

	public Environment getEnvironment() {
		return this.environment;
	}

	public boolean isInitialized() {
		return initialized;
	}
}
