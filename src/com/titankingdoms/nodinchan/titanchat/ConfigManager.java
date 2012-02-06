package com.titankingdoms.nodinchan.titanchat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class ConfigManager {
	
	private TitanChat plugin;
	
	public ConfigManager(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	// Adds the player to the admin list of the channel
	
	public void assignAdmin(Player player, String channelName) {
		List<String> admins = (!isEmpty("channels." + channelName + ".admins")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".admins") : new ArrayList<String>();
		
		admins.add(player.getName());
		
		plugin.getChannelConfig().set("channels." + channelName + ".admins", admins);
		plugin.saveChannelConfig();
	}
	
	// Bans the player
	
	public void ban(Player player, String channelName) {
		List<String> admins = (!isEmpty("channels." + channelName + ".admins")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".admins") : new ArrayList<String>();
		List<String> bans = (!isEmpty("channels." + channelName + ".black-list")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".black-list") : new ArrayList<String>();
		List<String> members = (!isEmpty("channels." + channelName + ".members")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".members") : new ArrayList<String>();
		
		admins.remove(player.getName());
		bans.add(player.getName());
		members.remove(player.getName());
		
		plugin.getChannelConfig().set("channels." + channelName + ".admins", admins);
		plugin.getChannelConfig().set("channels." + channelName + ".black-list", bans);
		plugin.getChannelConfig().set("channels." + channelName + ".members", members);
		plugin.saveChannelConfig();
	}
	
	// Creates the channel
	
	public void createChannel(Player player, String channelName) {
		assignAdmin(player, channelName);
		setTag(channelName, "[]");
		setChannelColour(channelName, "");
		setNameColour(channelName, "");
		setConvertColours(channelName, false);
		setStatus(channelName, "public");
		setFormat(channelName, "");
	}
	
	// Deletes the channel
	
	public void deleteChannel(String channelName) {
		plugin.getConfig().set("channels." + channelName, null);
		plugin.saveConfig();
		plugin.getChannelConfig().set("channels." + channelName, null);
		plugin.saveChannelConfig();
	}
	
	// Demotes the player
	
	public void demote(Player player, String channelName) {
		List<String> admins = (!isEmpty("channels." + channelName + ".admins")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".admins") : new ArrayList<String>();
		admins.remove(player.getName());
		plugin.getChannelConfig().set("channels." + channelName + ".admins", admins);
		whitelistMember(player, channelName);
		plugin.saveChannelConfig();
	}
	
	// Adds the phrase to the filter list
	
	public void filter(String phrase) {
		List<String> filter = (plugin.getConfig().getStringList("filter") != null) ? plugin.getConfig().getStringList("filter") : new ArrayList<String>();
		filter.add(phrase);
		plugin.getConfig().set("filter", filter);
		plugin.saveConfig();
	}
	
	// Adds the player to the follower list of the channel
	
	public void follow(Player player, String channelName) {
		List<String> followers = (!isEmpty("channels." + channelName + ".followers")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".followers") : new ArrayList<String>();
		
		followers.add(player.getName());
		
		plugin.getChannelConfig().set("channels." + channelName + ".followers", followers);
		plugin.saveChannelConfig();
	}
	
	// Check if a list exists for a channel
	
	public boolean isEmpty(String path) {
		return plugin.getChannelConfig().getStringList(path) == null;
	}
	
	// Promotes the player
	
	public void promote(Player player, String channelName) {
		List<String> members = (!isEmpty("channels." + channelName + ".members")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".members") : new ArrayList<String>();
		members.remove(player.getName());
		plugin.getChannelConfig().set("channels." + channelName + ".members", members);
		assignAdmin(player, channelName);
		plugin.saveChannelConfig();
	}
	
	// Sets the channel colour of the channel
	
	public void setChannelColour(String channelName, String colour) {
		plugin.getConfig().set("channels." + channelName + ".channel-display-colour", colour);
		plugin.saveConfig();
	}
	
	// Sets whether the channel converts colour codes
	
	public void setConvertColours(String channelName, boolean convert) {
		plugin.getConfig().set("channels." + channelName + ".colour-code", convert);
		plugin.saveConfig();
	}
	
	// Sets the format of the channel
	
	public void setFormat(String channelName, String format) {
		plugin.getConfig().set("channels." + channelName + ".format", format);
		plugin.saveConfig();
	}
	
	// Sets the name colour of the channel
	
	public void setNameColour(String channelName, String colour) {
		plugin.getConfig().set("channels." + channelName + ".name-display-colour", colour);
		plugin.saveConfig();
	}
	
	// Sets the password of the channel
	
	public void setPassword(String channelName, String password) {
		plugin.getConfig().set("channels." + channelName + ".password", password);
		plugin.saveConfig();
	}
	
	// Sets the status of the channel
	
	public void setStatus(String channelName, String status) {
		plugin.getConfig().set("channels." + channelName + ".status", status);
		plugin.saveConfig();
	}
	
	// Sets the tag of the channel
	
	public void setTag(String channelName, String tag) {
		plugin.getConfig().set("channels." + channelName + ".tag", tag);
		plugin.saveConfig();
	}
	
	// Unbans the player
	
	public void unban(Player player, String channelName) {
		List<String> bans = (!isEmpty("channels." + channelName + ".black-list")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".black-list") : new ArrayList<String>();
		bans.remove(player.getName());
		plugin.getChannelConfig().set("channels." + channelName + ".black-list", bans);
		
		whitelistMember(player, channelName);
		plugin.saveChannelConfig();
	}
	
	// Removes the player from the follower list of the channel
	
	public void unfollow(Player player, String channelName) {
		List<String> followers = (!isEmpty("channels." + channelName + ".followers")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".followers") : new ArrayList<String>();
		
		followers.remove(player.getName());
		
		plugin.getChannelConfig().set("channels." + channelName + ".followers", followers);
		plugin.saveChannelConfig();
	}
	
	// Adds the player to the member list of the channel
	
	public void whitelistMember(Player player, String channelName) {
		List<String> members = (!isEmpty("channels." + channelName + ".members")) ? plugin.getChannelConfig().getStringList("channels." + channelName + ".members") : new ArrayList<String>();
		
		members.add(player.getName());
		
		plugin.getChannelConfig().set("channels." + channelName + ".members", members);
		plugin.saveChannelConfig();
	}
}
