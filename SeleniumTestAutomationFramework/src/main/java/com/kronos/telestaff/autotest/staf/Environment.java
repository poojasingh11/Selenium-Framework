package com.kronos.telestaff.autotest.staf;


	public enum Environment {
		ENV1("5.4.0", "Test Env 1"), ENV2("5.3.0", "Test Env 2");
		private String key;
		private String name;
		
		/**
		 * @param name
		 */
		private Environment(String key, String name) {
			this.key = key;
			this.name = name;
		}
		
		/**
		 * @return instance key
		 */
		public String getKey() {
			return this.key;
		}
		
		/**
		 * @return instance name
		 */
		public String getName() {
			return this.name;
		}
		
		/**
		 * @param from
		 * 			String form of the environment (can be short or long form of name)
		 * 
		 * @return EnvironmentType representing the environment of the test execution
		 */
		public static Environment fromString(String from) {
			if (from != null) {
				for ( Environment e : values() ) {
					if(e.key.toLowerCase().equals(from.toLowerCase()) || e.name.toLowerCase().equals(from.toLowerCase())) {
						return e;
					}
				}
			}
			return null;
		}
		
		public String toString() {
			return this.name;
		}
	}


