package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandID {
	
	String name();
	
	String[] triggers();
	
	boolean requireChannel() default true;
}