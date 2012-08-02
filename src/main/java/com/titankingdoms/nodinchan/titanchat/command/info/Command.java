package com.titankingdoms.nodinchan.titanchat.command.info;

public @interface Command {
	
	boolean channel() default false;
	
	boolean server() default false;
}