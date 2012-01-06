package com.titankingdoms.nodinchan.titanchat;

import java.util.List;

import org.bukkit.entity.Player;

public class Channel {
	
	private TitanChat plugin;
	
	public Channel(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public boolean allowColours(String channelName) {
		return plugin.getConfig().getBoolean("channels." + channelName + ".allow-colours");
	}
	
	public String colourize(String message) {
		return message.replaceAll("(&([a-f0-9A-F]))", "\u00A7$2");
	}
	
	public String decolourize(String message) {
		String decolourized = "";
		decolourized = message.replace("&0", "");
		decolourized = decolourized.replace("&1", "");
		decolourized = decolourized.replace("&2", "");
		decolourized = decolourized.replace("&3", "");
		decolourized = decolourized.replace("&4", "");
		decolourized = decolourized.replace("&5", "");
		decolourized = decolourized.replace("&6", "");
		decolourized = decolourized.replace("&7", "");
		decolourized = decolourized.replace("&8", "");
		decolourized = decolourized.replace("&9", "");
		decolourized = decolourized.replace("&A", "");
		decolourized = decolourized.replace("&B", "");
		decolourized = decolourized.replace("&C", "");
		decolourized = decolourized.replace("&D", "");
		decolourized = decolourized.replace("&E", "");
		decolourized = decolourized.replace("&F", "");
		return decolourized;
	}
	
	public String format(Player player, String channeltag, String msg, boolean colourize) {
		String message = "";
		
		if (colourize) {
			message = channeltag + " " + player.getDisplayName() + ": " + colourize(msg);
			
		} else {
			message = channeltag + " " + player.getDisplayName() + ": " + decolourize(msg);
		}
		
		return message;
	}
	
	public String getChannelTag(Player player) {
		return plugin.getConfig().getString("channels." + plugin.getChannel(player) + ".channel-prefix");
	}
	
	public void relayMessage(String message, List<Player> participants) {
		for (Player player : participants) {
			player.sendMessage(format(player, message, getChannelTag(player), allowColours(plugin.getChannel(player))));
		}
	}
}
