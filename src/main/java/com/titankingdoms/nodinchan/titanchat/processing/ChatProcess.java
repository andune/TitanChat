package com.titankingdoms.nodinchan.titanchat.processing;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;

public final class ChatProcess implements Runnable {
	
	private final TitanChat plugin;
	
	private final Player sender;
	
	private final String message;
	
	public ChatProcess(Player sender, String message) {
		this.plugin = TitanChat.getInstance();
		this.sender = sender;
		this.message = message;
	}
	
	public void run() {
		Channel channel = plugin.getManager().getChannelManager().getChannel(sender);
		
		if (channel == null) {
			plugin.send(MessageLevel.WARNING, sender, "You are not in a channel, please join one to chat");
			return;
		}
		
		if (plugin.voiceless(sender, channel, true))
			return;
		
		String log = channel.sendMessage(sender, message);
		
		if (log != null && !log.equals(""))
			plugin.chatLog(log);
	}
}