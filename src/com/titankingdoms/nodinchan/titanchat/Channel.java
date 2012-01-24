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
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "\u00A7$2");
	}
	
	// Decolourizes the message
	
	public String decolourize(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "");
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
	
	public String format(Player player, String channeltag, String msg, boolean colourize) {
		String message = "";
		
		String tag = colourize(channeltag);
		String prefix = colourize(plugin.getPrefix(player));
		String suffix = colourize(plugin.getSuffix(player));
		
		if (!plugin.useDefaultFormat()) {
			message = plugin.getFormat();
			
			message = message.replace("%tag", tag);
			message = message.replace("%prefix", prefix);
			message = message.replace("%player", colourize(player.getDisplayName()));
			message = message.replace("%suffix", suffix);
			
			if (colourize) {
				message = message.replace("%message", colourize(filter(msg)));
				
			} else {
				message = message.replace("%message", decolourize(filter(msg)));
			}
			
		} else {
			if (colourize) {
				message = tag + " " + prefix + colourize(player.getDisplayName()) + suffix + ": " + colourize(filter(msg));
				
			} else {
				message = tag + " " + prefix + colourize(player.getDisplayName()) + suffix + ": " + decolourize(filter(msg));
			}
		}
		
		return message;
	}
	
	public String format(Player player, ChatColor name, ChatColor channel, String channeltag, String msg, boolean colourize) {
		String message = "";
		
		String tag = colourize(channeltag);
		String prefix = colourize(plugin.getPrefix(player));
		String suffix = colourize(plugin.getSuffix(player));
		
		if (!plugin.useDefaultFormat()) {
			message = plugin.getFormat();
			
			StringBuilder str = new StringBuilder();
			
			for (String word : message.split("%")) {
				if (str.length() < 1) {
					str.append(channel + word);
					
				} else {
					str.append(channel + "%" + word);
				}
			}
			
			message = str.toString();
			
			message = message.replace("%tag", tag);
			message = message.replace("%prefix", prefix);
			message = message.replace("%player", colourize(player.getDisplayName()));
			message = message.replace("%suffix", suffix);
			
			if (colourize) {
				message = message.replace("%message", colourize(filter(msg)));
				
			} else {
				message = message.replace("%message", decolourize(filter(msg)));
			}
			
		} else {
			if (colourize) {
				message = tag + " " + prefix + name + colourize(player.getDisplayName()) + suffix + channel + ": " + colourize(filter(msg));
				
			} else {
				message = tag + " " + prefix + name + colourize(player.getDisplayName()) + suffix + channel + ": " + decolourize(filter(msg));
			}
		}
		
		return message;
	}
	
	public String formatColourChannel(Player player, ChatColor channel, String channeltag, String msg, boolean colourize) {
		String message = "";
		
		String tag = colourize(channeltag);
		String prefix = colourize(plugin.getPrefix(player));
		String suffix = colourize(plugin.getSuffix(player));
		
		if (!plugin.useDefaultFormat()) {
			message = plugin.getFormat();
			
			StringBuilder str = new StringBuilder();
			
			for (String word : message.split("%")) {
				if (str.length() < 1) {
					str.append(channel + word);
					
				} else {
					str.append(channel + "%" + word);
				}
			}
			
			message = str.toString();
			
			message = message.replace("%tag", tag);
			message = message.replace("%prefix", prefix);
			message = message.replace("%player", colourize(player.getDisplayName()));
			message = message.replace("%suffix", suffix);
			
			if (colourize) {
				message = message.replace("%message", colourize(filter(msg)));
				
			} else {
				message = message.replace("%message", decolourize(filter(msg)));
			}
			
		} else {
			if (colourize) {
				message = tag + " " + prefix + colourize(player.getDisplayName()) + suffix + channel + ": " + colourize(filter(msg));
				
			} else {
				message = tag + " " + prefix + colourize(player.getDisplayName()) + suffix + channel + ": " + decolourize(filter(msg));
			}
		}
		
		return message;
	}
	
	public String formatColourName(Player player, ChatColor name, String channeltag, String msg, boolean colourize) {
		String message = "";
		
		String tag = colourize(channeltag);
		String prefix = colourize(plugin.getPrefix(player));
		String suffix = colourize(plugin.getSuffix(player));
		
		if (!plugin.useDefaultFormat()) {
			message = plugin.getFormat();
			
			message = message.replace("%tag", tag);
			message = message.replace("%prefix", prefix);
			message = message.replace("%player", colourize(player.getDisplayName()));
			message = message.replace("%suffix", suffix);
			
			if (colourize) {
				message = message.replace("%message", colourize(filter(msg)));
				
			} else {
				message = message.replace("%message", decolourize(filter(msg)));
			}
			
		} else {
			if (colourize) {
				message = tag + " " + prefix + name + colourize(player.getDisplayName()) + suffix + ": " + colourize(filter(msg));
				
			} else {
				message = tag + " " + prefix + name + colourize(player.getDisplayName()) + suffix + ": " + decolourize(filter(msg));
			}
		}
		
		return message;
	}
	
	// Gets the channel colour of the channel
	
	public ChatColor getChannelColour(Player player) {
		if (plugin.getConfig().getString("channels." + plugin.getChannel(player) + ".channel-colour").equalsIgnoreCase("NONE"))
			return null;
		
		return ChatColor.valueOf(plugin.getConfig().getString("channels." + plugin.getChannel(player) + ".channel-colour"));
	}
	
	// Gets the channel tag of the chat
	
	public String getChannelTag(Player player) {
		return plugin.getConfig().getString("channels." + plugin.getChannel(player) + ".channel-tag");
	}
	
	// Gets the name colour of the channel
	
	public ChatColor getNameColour(Player player) {
		if (plugin.getConfig().getString("channels." + plugin.getChannel(player) + ".name-colour").equalsIgnoreCase("NONE"))
			return null;
		
		return ChatColor.valueOf(plugin.getConfig().getString("channels." + plugin.getChannel(player) + ".name-colour"));
	}
}
