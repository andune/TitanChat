package com.titankingdoms.nodinchan.titanchat.util;

import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.support.TCSupport;

public class Format {
	
	private TitanChat plugin;
	
	public Format(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	// Format for broadcasting
	
	public String broadcast(Player player, String msg) {
		String message = "";
		
		String tag = plugin.getConfig().getString("broadcast.tag");
		String prefix = plugin.getPlayerPrefix(player);
		String suffix = plugin.getPlayerSuffix(player);
		String colour = plugin.getConfig().getString("broadcast.chat-display-colour");
		
		message = colourize(tag + " " + prefix + colour + player.getDisplayName() + suffix + "&f: " + colour + msg);
		
		return message;
	}
	
	// Checks if colour codes will be converted on the channel
	
	public boolean colours(String channelName) {
		if (plugin.getStaffChannel().equals(channelName))
			return true;
		
		return plugin.getConfig().getBoolean("channels." + channelName + ".colour-code");
	}
	
	// Colourizes the message
	
	public String colourize(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "\u00A7$2");
	}
	
	// Decolourizes the message
	
	public String decolourize(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "");
	}
	
	// Filter
		
	public String filter(String line) {
		if (!plugin.getConfig().getBoolean("filter.enable"))
			return line;
		
		String filtered = line;
		String censor = plugin.getConfig().getString("filter.censor");
		
		if (plugin.getConfig().getStringList("filter.phrases") != null) {
			for (String word : plugin.getConfig().getStringList("filter")) {
				if (line.toLowerCase().contains(word.toLowerCase())) {
					filtered = line.replaceAll("(?i)" + word, word.replaceAll("[A-Za-z]", censor));
				}
			}
		}
			
		return filtered;
	}
	
	// Formats the message
	
	public String format(Player player, String channelName, String msg) {
		String message = "";
		
		String name = player.getDisplayName();
		String tag = getTag(channelName);
		String playerPrefix = plugin.getPlayerPrefix(player);
		String playerSuffix = plugin.getPlayerSuffix(player);
		String groupPrefix = plugin.getGroupPrefix(player);
		String groupSuffix = plugin.getGroupSuffix(player);
		String channelColour = getChannelDisplayColour(channelName);
		String nameColour = getNameDisplayColour(channelName);
		
		if (plugin.useDefaultFormat()) {
			if (colours(channelName) || plugin.has(player, "TitanChat.colours")) {
				message = colourize(tag + " " + playerPrefix + nameColour + name + playerSuffix + "&f: " + channelColour + filter(msg));
				
			} else {
				message = colourize(tag + " " + playerPrefix + nameColour + name + playerSuffix + "&f: " + channelColour) + decolourize(filter(msg));
			}
			
		} else {
			message = plugin.getFormat(channelName);
			
			message = message.replace("%tag", tag);
			message = message.replace("%pprefix", playerPrefix);
			message = message.replace("%gprefix", groupPrefix);
			message = message.replace("%player", nameColour + name);
			message = message.replace("%psuffix", playerSuffix);
			message = message.replace("%gsuffix", groupSuffix);
			
			for (TCSupport support : plugin.getSupports()) {
				try {
					message = support.chatMade(player, message);
					
				} catch (Exception e) {
					Logger.getLogger("TitanLog").info("Exception caught while sending message to the support");
				}
			}
			
			message = colourize(message);
			
			if (colours(channelName) || plugin.has(player, "TitanChat.colours")) {
				message = message.replace("%message", colourize(channelColour + filter(msg)));
				
			} else {
				message = message.replace("%message", colourize(channelColour) + decolourize(filter(msg)));
			}
		}
		
		return message;
	}
	
	// Gets the display colour for the channel
	
	public String getChannelDisplayColour(String channelName) {
		return plugin.getConfig().getString("channels." + channelName + ".channel-display-colour");
	}
	
	// Gets the display colour for the name
	
	public String getNameDisplayColour(String channelName) {
		return plugin.getConfig().getString("channels." + channelName + ".name-display-colour");
	}
	
	// Gets the tag of the channel
	
	public String getTag(String channelName) {
		return plugin.getConfig().getString("channels." + channelName + ".tag");
	}
	
	// Format for local
	
	public String local(Player player, String msg) {
		String message = "";
		
		String tag = plugin.getConfig().getString("local.tag");
		String prefix = plugin.getPlayerPrefix(player);
		String suffix = plugin.getPlayerSuffix(player);
		String colour = plugin.getConfig().getString("local.chat-display-colour");
		
		message = colourize(tag + " " + prefix + colour + player.getDisplayName() + suffix + "&f: " + colour + msg);
		
		return message;
	}
}
