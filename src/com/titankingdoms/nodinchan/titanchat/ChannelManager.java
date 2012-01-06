package com.titankingdoms.nodinchan.titanchat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class ChannelManager {
	
	private TitanChat plugin;
	
	public ChannelManager(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public void assignAdmins(List<Player> players, String channelName) {
		for (Player player : players) {
			String name = player.getName();
			List<String> admins = plugin.getChannelConfig().getStringList("channels." + channelName + ".admins");
			admins.add(name);
			plugin.getChannelConfig().set("channels." + channelName + ".admins", admins);
		}
	}
	
	public void banMember(Player player, String channelName) {
		String name = player.getName();
		List<String> members = plugin.getChannelConfig().getStringList("channels." + channelName + ".members");
		members.remove(name);
		plugin.getChannelConfig().set("channels." + channelName + ".members", members);
	}
	
	public void createChannel(Player player, String channelName) {
		List<Player> admins = new ArrayList<Player>();
		admins.add(player);
		plugin.getChannelConfig().set("channels." + channelName + ".admins", admins);
		plugin.getConfig().set("channels." + channelName + ".channel-prefix", "");
		plugin.getConfig().set("channels." + channelName + ".channel-colour", "WHITE");
		plugin.getConfig().set("channels." + channelName + ".allow-colours", false);
	}
	
	public void setColour(String channelName, String colour) {
		plugin.getConfig().set("channels." + channelName + ".channel-colour", colour);
	}
	
	public void setPrefix(String channelName, String prefix) {
		plugin.getConfig().set("channels." + channelName + ".channel-prefix", prefix);
	}
	
	public void whitelistMembers(List<Player> players, String channelName) {
		for (Player player : players) {
			String name = player.getName();
			List<String> members = plugin.getChannelConfig().getStringList("channels." + channelName + ".members");
			members.add(name);
			plugin.getChannelConfig().set("channels." + channelName + ".members", members);
		}
	}
}
