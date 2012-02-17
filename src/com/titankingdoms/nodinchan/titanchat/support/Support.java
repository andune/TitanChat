package com.titankingdoms.nodinchan.titanchat.support;

import org.bukkit.entity.Player;

public interface Support {
	
	public void chatMade(String name, String message);
	
	public String chatMade(Player player, String message);
}
