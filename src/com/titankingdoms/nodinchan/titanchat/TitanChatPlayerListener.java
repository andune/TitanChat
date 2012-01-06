package com.titankingdoms.nodinchan.titanchat;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class TitanChatPlayerListener extends PlayerListener {
	
	private TitanChat plugin;
	
	public TitanChatPlayerListener(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled())
			return;
	}
}
