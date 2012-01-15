package com.titankingdoms.nodinchan.titanchat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Channel {
	
	private TitanChat plugin;
	
	public Channel(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	// Checks if colour codes are allowed on the channel
	
	public boolean allowColours(Player player) {
		if (plugin.getStaffChannel() == plugin.getChannel(player))
			return true;
		
		return plugin.getConfig().getBoolean("channels." + plugin.getChannel(player) + ".allow-colours");
	}
	
	// Colourizes the message
	
	public String colourize(String message) {
		return message.replaceAll("(&([a-f0-9A-F]))", "\u00A7$2");
	}
	
	// Decolourizes the message
	
	public String decolourize(String message) {
		return message.replaceAll("(&([a-f0-9A-F]))", "");
	}
	
	// Formats the message
	
	public String format(Player player, ChatColor colour, String channeltag, String msg, boolean colourize) {
		String message = "";
		
		String tag = colourize(channeltag);
		String prefix = colourize(plugin.getPrefix(player));
		String suffix = colourize(plugin.getSuffix(player));
		
		if (colourize) {
			message = tag + " " + prefix + colour + colourize(player.getDisplayName()) + suffix + colour + ": " + colourize(msg);
			
		} else {
			message = tag + " " + prefix + colour + colourize(player.getDisplayName()) + suffix + colour + ": " + decolourize(msg);
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
