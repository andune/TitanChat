package com.titankingdoms.nodinchan.titanchat.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.event.util.Message;

public final class ChatPacket {
	
	private final TitanChat plugin;
	
	private final Map<String, Message> messages;
	
	public ChatPacket(Map<String, Message> chat) {
		this.plugin = TitanChat.getInstance();
		this.messages = new HashMap<String, Message>();
		
		if (chat != null)
			messages.putAll(chat);
	}
	
	public void chat() {
		for (Entry<String, Message> entry : messages.entrySet()) {
			Player recipant = plugin.getPlayer(entry.getKey());
			
			if (recipant == null)
				continue;
			
			Message message = entry.getValue();
			String[] lines = plugin.getFormatHandler().splitAndFormat(message.getFormat(), "%message", message.getMessage());
			recipant.sendMessage(lines);
		}
	}
}