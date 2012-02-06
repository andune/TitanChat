package com.titankingdoms.nodinchan.titanchat;

import org.bukkit.entity.Player;

public class Format {
	
	private TitanChat plugin;
	
	public Format(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	// Format for broadcasting
	
	public String broadcast(Player player, String msg) {
		String message = "";
		
		String tag = colourize(plugin.getConfig().getString("broadcast.tag"));
		String prefix = colourize(plugin.getPrefix(player));
		String suffix = colourize(plugin.getSuffix(player));
		String colour = colourize(plugin.getConfig().getString("broadcast.chat-display-colour"));
		
		message = tag + " " + prefix + colour + player.getDisplayName() + suffix + colourize("&f: ") + colour + colourize(msg);
		
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
		
		String name = colourize(player.getDisplayName());
		String tag = colourize(getTag(channelName));
		String prefix = colourize(plugin.getPrefix(player));
		String suffix = colourize(plugin.getSuffix(player));
		String channelColour = colourize(getChannelDisplayColour(channelName));
		String nameColour = colourize(getNameDisplayColour(channelName));
		
		if (plugin.useDefaultFormat()) {
			if (colours(channelName) || plugin.has(player, "TitanChat.colours")) {
				message = tag + " " + prefix + nameColour + name + suffix + colourize("&f: ") + channelColour + colourize(filter(msg));
				
			} else {
				message = tag + " " + prefix + nameColour + name + suffix + colourize("&f: ") + channelColour + decolourize(filter(msg));
			}
			
		} else {
			message = plugin.getFormat(channelName);
			
			message = message.replace("%tag", tag);
			message = message.replace("%prefix", prefix);
			message = message.replace("%player", nameColour + name);
			message = message.replace("%suffix", suffix);
			
			message = colourize(message);
			
			if (colours(channelName) || plugin.has(player, "TitanChat.colours")) {
				message = message.replace("%message", channelColour + colourize(filter(msg)));
				
			} else {
				message = message.replace("%message", channelColour + decolourize(filter(msg)));
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
		
		String tag = colourize(plugin.getConfig().getString("local.tag"));
		String prefix = colourize(plugin.getPrefix(player));
		String suffix = colourize(plugin.getSuffix(player));
		String colour = colourize(plugin.getConfig().getString("local.chat-display-colour"));
		
		message = tag + " " + prefix + colour + player.getDisplayName() + suffix + colourize("&f: ") + colour + msg;
		
		return message;
	}
}
