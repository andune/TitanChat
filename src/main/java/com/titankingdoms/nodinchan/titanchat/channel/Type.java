package com.titankingdoms.nodinchan.titanchat.channel;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.titankingdoms.nodinchan.titanchat.debug.Debugger;

/**
 * Type - Types of Channels
 * 
 * @author NodinChan
 *
 */
public enum Type {
	CUSTOM("custom"),
	DEFAULT("default"),
	PASSWORD("password"),
	PRIVATE("private"),
	PUBLIC("public"),
	STAFF("staff"),
	UNKNOWN("unknown");
	
	private String name;

	private final static Debugger db = new Debugger(3);
	private static final Map<String, Type> NAME_MAP = new HashMap<String, Type>();
	
	private Type(String name) {
		this.name = name;
	}
	
	static {
		for (Type type : EnumSet.allOf(Type.class)) {
			db.i("Adding Type: " + type.name);
			NAME_MAP.put(type.name, type);
		}
	}
	
	public static Type fromName(String name) {
		return NAME_MAP.get(name);
	}
	
	public String getName() {
		return name;
	}
}