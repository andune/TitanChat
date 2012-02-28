package com.titankingdoms.nodinchan.titanchat.support;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public abstract class TCSupport implements Support {
	
	protected TitanChat plugin;
	
	private String name;
	
	public TCSupport(TitanChat plugin, String name) {
		this.plugin = plugin;
		this.name = name;
	}

	@Override
	public void chatMade(String name, String message) {}

	@Override
	public String chatMade(Player player, String message) {
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void init();
}