package com.titankingdoms.nodinchan.titanchat.util;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelVariables;

public final class Format {

	private final TitanChat plugin;
	
	public Format(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public String broadcastFormat(Player player, String msg) {
		String message = plugin.getConfig().getString("broadcast.player.format");
		
		String playerPrefix = plugin.getPlayerPrefix(player);
		String playerSuffix = plugin.getPlayerSuffix(player);
		String groupPrefix = plugin.getGroupPrefix(player);
		String groupSuffix = plugin.getGroupSuffix(player);
		
		message = message.replace("%pprefix", playerPrefix);
		message = message.replace("%gprefix", groupPrefix);
		message = message.replace("%player", player.getDisplayName());
		message = message.replace("%psuffix", playerSuffix);
		message = message.replace("%gsuffix", groupSuffix);
		
		message = plugin.getAddonManager().executeFormat(player, message);
		
		StringBuilder str = new StringBuilder();
		
		for (String word : message.split(" ")) {
			if (str.length() > 0)
				str.append(" ");
			
			str.append(colourise(word));
		}
		
		message = str.toString();
		
		message = message.replace("%message", colourise(msg));
		
		return message;
	}
	
	public String colourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "\u00A7$2");
	}
	
	public boolean colours(String name) {
		if (plugin.getChannelManager().getStaffChannel().equals(plugin.getChannelManager().getChannel(name)))
			return true;
		
		return plugin.getChannelManager().getChannel(name).getVariables().convert();
	}
	
	public String decolourise(String message) {
		return message.replaceAll("(&([a-f0-9A-F|kK]))", "");
	}
	
	public String emoteFormat(Player player, String msg) {
		String message = plugin.getConfig().getString("emote.player.format");
		
		String playerPrefix = plugin.getPlayerPrefix(player);
		String playerSuffix = plugin.getPlayerSuffix(player);
		String groupPrefix = plugin.getGroupPrefix(player);
		String groupSuffix = plugin.getGroupSuffix(player);

		message = message.replace("%pprefix", playerPrefix);
		message = message.replace("%gprefix", groupPrefix);
		message = message.replace("%player", player.getDisplayName());
		message = message.replace("%psuffix", playerSuffix);
		message = message.replace("%gsuffix", groupSuffix);
		
		message = plugin.getAddonManager().executeFormat(player, message);
		
		StringBuilder str = new StringBuilder();
		
		for (String word : message.split(" ")) {
			if (str.length() > 0)
				str.append(" ");
			
			str.append(colourise(word));
		}
		
		message = str.toString();
		
		message = message.replace("%action", colourise(msg));
		
		return message;
	}
	
	public String format(Player player) {
		String message = "";
		
		String playerPrefix = plugin.getPlayerPrefix(player);
		String playerSuffix = plugin.getPlayerSuffix(player);
		String groupPrefix = plugin.getGroupPrefix(player);
		String groupSuffix = plugin.getGroupSuffix(player);
		String chatColour = plugin.getConfig().getString("channels.chat-display-colour");
		
		if (plugin.useDefaultFormat()) {
			message = colourise("<" + playerPrefix + "&1$s" + playerSuffix + "&f> " + chatColour + "%2$s");
			
		} else {
			message = plugin.getConfig().getString("formatting.format");

			message = message.replace("%pprefix", playerPrefix);
			message = message.replace("%gprefix", groupPrefix);
			message = message.replace("%player", "%1$s");
			message = message.replace("%psuffix", playerSuffix);
			message = message.replace("%gsuffix", groupSuffix);
			
			message = plugin.getAddonManager().executeFormat(player, message);
			
			StringBuilder str = new StringBuilder();
			
			for (String word : message.split(" ")) {
				if (str.length() > 0)
					str.append(" ");
				
				str.append(colourise(word));
			}
			
			message = str.toString();
			
			message = message.replace("%message", "%2$s");
		}
		
		return message;
	}
	
	public String format(Player player, String channel, String msg) {
		String message = "";
		
		ChannelVariables variables = plugin.getChannelManager().getChannel(channel).getVariables();
		
		String name = player.getDisplayName();
		String tag = variables.getTag();
		String playerPrefix = variables.getPlayerPrefix(player);
		String playerSuffix = variables.getPlayerSuffix(player);
		String groupPrefix = variables.getGroupPrefix(player);
		String groupSuffix = variables.getGroupSuffix(player);
		String chatColour = variables.getChatColour();
		String nameColour = variables.getNameColour();
		
		if (plugin.useDefaultFormat()) {
			if (colours(channel) || plugin.has(player, "TitanChat.colours"))
				message = colourise(tag + " " + playerPrefix + nameColour + name + playerSuffix + "&f: " + chatColour + msg);
			else
				message = colourise(tag + " " + playerPrefix + nameColour + name + playerSuffix + "&f: " + chatColour) + decolourise(msg);
			
		} else {
			message = variables.getFormat();
			
			message = message.replace("%tag", tag);
			message = message.replace("%pprefix", playerPrefix);
			message = message.replace("%gprefix", groupPrefix);
			message = message.replace("%player", nameColour + name);
			message = message.replace("%psuffix", playerSuffix);
			message = message.replace("%gsuffix", groupSuffix);
			
			message = plugin.getAddonManager().executeFormat(player, message);
			
			StringBuilder str = new StringBuilder();
			
			for (String word : message.split(" ")) {
				if (str.length() > 0)
					str.append(" ");
				
				str.append(colourise(word));
			}
			
			message = str.toString();
			
			if (colours(channel) || plugin.has(player, "TitanChat.colours"))
				message = message.replace("%message", colourise(chatColour + msg));
			else
				message = message.replace("%message", colourise(chatColour) + decolourise(msg));
		}
		
		return message;
	}
}