package com.titankingdoms.nodinchan.titanchat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Channel {
	
	private TitanChat plugin;
	
	public Channel(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	// Checks if colour codes are allowed on the channel
	
	public boolean allowColours(String channelName) {
		if (plugin.getStaffChannel() == channelName)
			return true;
		
		return plugin.getConfig().getBoolean("channels." + channelName + ".allow-colours");
	}
	
	// Colourizes the message
	
	public String colourize(String message) {
		return message.replaceAll("(&([a-f0-9A-F]))", "\u00A7$2");
	}
	
	// Decolourizes the message
	
	public String decolourize(String message) {
		return message.replaceAll("(&([a-f0-9A-F]))", "");
	}
	
	// Filter
	
	public String filter(String line) {
		String filtered = line;
		
		if (plugin.getConfig().getStringList("filter") != null) {
			for (String word : plugin.getConfig().getStringList("filter")) {
				if (line.toLowerCase().contains(word.toLowerCase())) {
					filtered = line.replaceAll("(?i)" + word, word.replaceAll("[A-Za-z]", "*"));
				}
			}
		}
		
		return filtered;
	}
	
	// Formats the message
	
	public String format(Player player, ChatColor colour, String channeltag, String msg, boolean colourize) {
		String message = "";
		
		String tag = colourize(channeltag);
		String prefix = colourize(plugin.getPrefix(player));
		String suffix = colourize(plugin.getSuffix(player));
		
		if (colourize) {
			message = tag + " " + prefix + colour + colourize(player.getDisplayName()) + suffix + colour + ": " + colourize(filter(msg));
			
		} else {
			message = tag + " " + prefix + colour + colourize(player.getDisplayName()) + suffix + colour + ": " + decolourize(filter(msg));
		}
		
		return message;
	}
	
	// Gets the default colour of the chat
	
	public ChatColor getChannelColour(Player player) {
		return ChatColor.valueOf(plugin.getConfig().getString("channels." + plugin.getChannel(player) + ".channel-colour"));
	}
	
	// Gets the channel tag of the chat
	
	public String getChannelTag(Player player) {
		return plugin.getConfig().getString("channels." + plugin.getChannel(player) + ".channel-tag");
	}
}
