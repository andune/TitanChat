package com.titankingdoms.nodinchan.titanchat.util;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.support.Support;

public class Format {
	
	private TitanChat plugin;
	
	public Format(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public String broadcastFormat(Player player, String msg) {
		String message = "";
		
		String tag = plugin.getConfig().getString("broadcast.tag");
		String prefix = plugin.getPlayerPrefix(player);
		String suffix = plugin.getPlayerSuffix(player);
		String colour = plugin.getConfig().getString("broadcast.chat-display-colour");
		
		message = colourise(tag + " " + prefix + colour + player.getDisplayName() + suffix + "&f: " + colour + msg);
		
		return message;
	}
	
	public String colourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "\u00A7$2");
	}
	
	public boolean colours(String channelName) {
		if (plugin.getStaffChannel().equals(channelName))
			return true;
		
		return plugin.getConfig().getBoolean("channels." + channelName + ".colour-code");
	}
	
	public String decolourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "");
	}
	
	public String format(Player player, String channelName, String msg) {
		String message = "";
		
		String name = player.getDisplayName();
		String tag = get(channelName, "tag");
		String playerPrefix = plugin.getPlayerPrefix(player);
		String playerSuffix = plugin.getPlayerSuffix(player);
		String groupPrefix = plugin.getGroupPrefix(player);
		String groupSuffix = plugin.getGroupSuffix(player);
		String channelColour = get(channelName, "chat-display-colour");
		String nameColour = get(channelName, "name-display-colour");
		
		if (plugin.useDefaultFormat()) {
			if (colours(channelName) || plugin.has(player, "TitanChat.colours"))
				message = colourise(tag + " " + playerPrefix + nameColour + name + playerSuffix + "&f: " + channelColour + msg);
			else
				message = colourise(tag + " " + playerPrefix + nameColour + name + playerSuffix + "&f: " + channelColour) + decolourise(msg);
			
		} else {
			message = plugin.getFormat(channelName);
			
			message = message.replace("%tag", tag);
			message = message.replace("%pprefix", playerPrefix);
			message = message.replace("%gprefix", groupPrefix);
			message = message.replace("%player", nameColour + name);
			message = message.replace("%psuffix", playerSuffix);
			message = message.replace("%gsuffix", groupSuffix);
			
			message = runSupports(player, message);
			
			StringBuilder str = new StringBuilder();
			
			for (String word : message.split(" ")) {
				if (str.length() > 0)
					str.append(" ");
				
				str.append(colourise(word));
			}
			
			message = str.toString();
			
			if (colours(channelName) || plugin.has(player, "TitanChat.colours"))
				message = message.replace("%message", colourise(channelColour + msg));
			else
				message = message.replace("%message", colourise(channelColour) + decolourise(msg));
		}
		
		return message;
	}
	
	public String get(String channelName, String item) {
		return plugin.getConfig().getString("channels." + channelName + "." + item);
	}
	
	public String runSupports(Player player, String message) {
		String msg = message;
		
		for (Support support : plugin.getSupports()) {
			msg = support.chatMade(player, message);
		}
		
		return msg;
	}
}