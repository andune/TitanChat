package com.titankingdoms.nodinchan.titanchat.support;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

public abstract interface AddonInterface {
	
	public void chatMade(String name, String message);
	
	public String chatMade(Player player, String message);
	
	public Logger getLogger(String name);
	
	public String getName();
	
	public void init();
	
	public boolean onCommand(Player player, String cmd, String[] args);
}