package com.titankingdoms.nodinchan.titanchat.permissions;

import java.util.ArrayList;
import java.util.List;

public class Group {
	
	private String name;
	private String prefix;
	private String suffix;
	
	private List<String> permissions;
	private List<User> users;
	
	public Group(String name) {
		this.name = name;
		this.prefix = "";
		this.suffix = "";
		this.permissions = new ArrayList<String>();
		this.users = new ArrayList<User>();
	}
	
	public String getName() {
		return name;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getSuffix() {
		return suffix;
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public User getUser(String name) {
		for (User user : users) {
			if (user.getName().equals(name))
				return user;
		}
		
		return null;
	}
	
	public List<User> getUsers() {
		return users;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}