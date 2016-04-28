package com.kronos.telestaff.autotest.staf;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TestEnvironment {
	Environment value() default Environment.ENV1;
}

