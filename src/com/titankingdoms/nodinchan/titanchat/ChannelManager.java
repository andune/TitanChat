package com.titankingdoms.nodinchan.titanchat;

import java.util.ArrayList;
import java.util.List;

public class ChannelManager {
	
	private TitanChat plugin;
	
	public ChannelManager(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	// Adds an Admin to the list of Admins in channels.yml
	
	public void assignAdmin(String name, String channelName) {
		if (plugin.getChannelConfig().getStringList("channels." + channelName + ".admins") != null) {
			List<String> admins = plugin.getChannelConfig().getStringList("channels." + channelName + ".admins");
			admins.add(name);
			plugin.getChannelConfig().set("channels." + channelName + ".admins", admins);
			
		} else {
			List<String> admins = new ArrayList<String>();
			admins.add(name);
			plugin.getChannelConfig().set("channels." + channelName + ".admins", admins);
		}
		
		plugin.saveChannelConfig();
	}
	
	// Removes the player from the list of members or adds them to the blacklist
	
	public void ban(String name, String channelName) {
		if (plugin.isPublic(channelName)) {
			if (plugin.getChannelConfig().getStringList("channels." + channelName + ".black-list") != null) {
				List<String> blacklist = plugin.getChannelConfig().getStringList("channels." + channelName + ".black-list");
				blacklist.add(name);
				plugin.getChannelConfig().set("channels." + channelName + ".black-list", blacklist);
				
			} else {
				List<String> blacklist = new ArrayList<String>();
				blacklist.add(name);
				plugin.getChannelConfig().set("channels." + channelName + ".black-list", blacklist);
			}
			
		} else {
			List<String> members = plugin.getChannelConfig().getStringList("channels." + channelName + ".members");
			members.remove(name);
			plugin.getChannelConfig().set("channels." + channelName + ".members", members);
		}
		
		plugin.saveChannelConfig();
	}
	
	// Creates a default channel config to be modified
	
	public void createChannel(String name, String channelName) {
		assignAdmin(name, channelName);
		setAllowColours(channelName, false);
		setColour(channelName, "WHITE");
		setTag(channelName, "");
		setPublic(channelName, true);
		plugin.saveConfig();
	}
	
	public void deleteChannel(String channelName) {
		plugin.getConfig().set("channels." + channelName, null);
		plugin.saveConfig();
		plugin.getChannelConfig().set("channels." + channelName, null);
		plugin.saveChannelConfig();
	}
	
	// Demotes a player by changing the config
	
	public void demote(String name, String channelName) {
		List<String> admins = plugin.getChannelConfig().getStringList("channels." + channelName + ".admins");
		admins.remove(name);
		plugin.getChannelConfig().set("channels." + channelName + ".admins", admins);
		whitelistMember(name, channelName);
		plugin.saveChannelConfig();
	}
	
	// Promotes a player by changing the config
	
	public void promote(String name, String channelName) {
		List<String> members = plugin.getChannelConfig().getStringList("channels." + channelName + ".members");
		members.remove(name);
		plugin.getChannelConfig().set("channels." + channelName + ".members", members);
		assignAdmin(name, channelName);
		plugin.saveChannelConfig();
	}
	
	// Sets whether colour codes are allowed on the channel
	
	public void setAllowColours(String channelName, boolean allow) {
		plugin.getConfig().set("channels." + channelName + ".allow-colours", allow);
		plugin.saveConfig();
	}
	
	// Sets the default chat colour of the channel
	
	public void setColour(String channelName, String colour) {
		plugin.getConfig().set("channels." + channelName + ".channel-colour", colour);
		plugin.saveConfig();
	}
	
	// Sets the tag of the channel
	
	public void setTag(String channelName, String tag) {
		plugin.getConfig().set("channels." + channelName + ".channel-tag", tag);
		plugin.saveConfig();
	}
	
	// Sets whether players need to be whitelisted to join the channel
	
	public void setPublic(String channelName, boolean whitelist) {
		plugin.getConfig().set("channels." + channelName + ".public", whitelist);
		plugin.saveConfig();
	}
	
	// Removes the player from the black-list and adds them to the list of members if necessary
	
	public void unban(String name, String channelName) {
		List<String> blacklist = plugin.getChannelConfig().getStringList("channels." + channelName + ".black-list");
		blacklist.remove(name);
		plugin.getChannelConfig().set("channels." + channelName + ".black-list", blacklist);
		
		if (!plugin.isPublic(channelName)) {
			whitelistMember(name, channelName);
		}
		
		plugin.saveChannelConfig();
	}
	
	// Adds a Member to the list of Members in channels.yml
	
	public void whitelistMember(String name, String channelName) {
		if (plugin.getChannelConfig().getStringList("channels." + channelName + ".members") != null) {
			List<String> members = plugin.getChannelConfig().getStringList("channels." + channelName + ".members");
			members.add(name);
			plugin.getChannelConfig().set("channels." + channelName + ".members", members);
			
		} else {
			List<String> members = new ArrayList<String>();
			members.add(name);
			plugin.getChannelConfig().set("channels." + channelName + ".members", members);
		}
		
		plugin.saveChannelConfig();
	}
}
