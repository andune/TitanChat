package com.titankingdoms.nodinchan.titanchat.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public final class ChatPacket {
	
	private final TitanChat plugin;
	
	private final Map<String, String[]> messages;
	
	public ChatPacket(Map<String, String[]> chat) {
		this.plugin = TitanChat.getInstance();
		this.messages = new HashMap<String, String[]>();
		
		if (chat != null)
			messages.putAll(chat);
	}
	
	public void chat() {
		for (Entry<String, String[]> entry : messages.entrySet()) {
			Player recipant = plugin.getPlayer(entry.getKey());
			
			if (recipant == null)
				continue;
			
			recipant.sendMessage(entry.getValue());
		}
	}
}