package com.titankingdoms.nodinchan.titanchat.permissions;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String name;
	private String prefix;
	private String suffix;
	
	private Group group;
	
	private List<String> permissions;
	
	public User(String name, Group group) {
		this.name = name;
		this.prefix = "";
		this.suffix = "";
		this.group = group;
		this.permissions = new ArrayList<String>();
	}
	
	public Group getGroup() {
		return group;
	}
	
	public String getName() {
		return name;
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}