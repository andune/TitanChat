package com.titankingdoms.nodinchan.titanchat.support;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public abstract class Command {
	
	private String name;
	private String[] aliases;
	
	public Command(String name) {
		this.name = name;
		this.aliases = new String[] {};
	}
	
	public abstract boolean execute(Player player, Command cmd, String[] args);
	
	public List<String> getAliases() {
		List<String> aliases = new ArrayList<String>();
		
		for (String alias : this.aliases) {
			aliases.add(alias);
		}
		
		return aliases;
	}
	
	public String getName() {
		return name;
	}
	
	public Command register() {
		return this;
	}
	
	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}
}