package com.titankingdoms.nodinchan.titanchat.support;

import org.bukkit.entity.Player;

public abstract class Command {
	
	private String name;
	
	public Command(String name) {
		this.name = name;
	}
	
	public abstract boolean execute(Player player, String cmd, String[] args);
	
	public String getName() {
		return name;
	}
	
	public Command register() {
		return this;
	}
}