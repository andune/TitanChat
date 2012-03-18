package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CommandInfo - Additional information
 * 
 * @author NodinChan
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
	
	String description();
	
	String usage();
}