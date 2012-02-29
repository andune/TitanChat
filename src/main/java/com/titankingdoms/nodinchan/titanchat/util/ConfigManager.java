package com.titankingdoms.nodinchan.titanchat.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class ConfigManager {
	
	private TitanChat plugin;
	
	public ConfigManager(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public void assignAdmin(Player player, String channelName) {
		List<String> adminList = (!isEmpty(plugin.getExactName(channelName), "admins")) ? get(plugin.getExactName(channelName), "admins") : new ArrayList<String>();
		adminList.add(player.getName());
		set(plugin.getExactName(channelName), "admins", adminList);
	}
	
	public void ban(Player player, String channelName) {
		List<String> adminList = (!isEmpty(plugin.getExactName(channelName), "admins")) ? get(plugin.getExactName(channelName), "admins") : new ArrayList<String>();
		List<String> blacklist = (!isEmpty(plugin.getExactName(channelName), "blacklist")) ? get(plugin.getExactName(channelName), "blacklist") : new ArrayList<String>();
		List<String> whitelist = (!isEmpty(plugin.getExactName(channelName), "whitelist")) ? get(plugin.getExactName(channelName), "whitelist") : new ArrayList<String>();
		
		adminList.remove(player.getName());
		blacklist.add(player.getName());
		whitelist.remove(player.getName());
		
		set(plugin.getExactName(channelName), "admins", adminList);
		set(plugin.getExactName(channelName), "blacklist", blacklist);
		set(plugin.getExactName(channelName), "whitelist", whitelist);
	}
	
	public void createChannel(Player player, String channelName) {
		assignAdmin(player, plugin.getExactName(channelName));
		setTag(plugin.getExactName(channelName), "[]");
		setChannelColour(plugin.getExactName(channelName), "");
		setNameColour(plugin.getExactName(channelName), "");
		setConvertColours(plugin.getExactName(channelName), false);
		setType(plugin.getExactName(channelName), "public");
		setFormat(plugin.getExactName(channelName), "");
	}
	
	public void deleteChannel(String channelName) {
		plugin.getConfig().set("channels." + plugin.getExactName(channelName), null);
		plugin.saveConfig();
		plugin.getChannelConfig().set("channels." + plugin.getExactName(channelName), null);
		plugin.saveChannelConfig();
	}
	
	public void demote(Player player, String channelName) {
		List<String> adminList = (!isEmpty(plugin.getExactName(channelName), "admins")) ? get(plugin.getExactName(channelName), "admins") : new ArrayList<String>();
		adminList.remove(player.getName());
		set(plugin.getExactName(channelName), "admins", adminList);
	}
	
	public boolean enableJoinMessages() {
		return plugin.getConfig().getBoolean("channel-messages.join");
	}
	
	public boolean enableLeaveMessages() {
		return plugin.getConfig().getBoolean("channel-messages.leave");
	}
	
	public void follow(Player player, String channelName) {
		List<String> followerList = (!isEmpty(plugin.getExactName(channelName), "followers")) ? get(plugin.getExactName(channelName), "followers") : new ArrayList<String>();
		followerList.add(player.getName());
		set(plugin.getExactName(channelName), "followers", followerList);
	}
	
	public List<String> get(String channelName, String path) {
		return plugin.getChannelConfig().getStringList("channels." + plugin.getExactName(channelName) + "." + path);
	}
	
	public boolean isEmpty(String channelName, String path) {
		return plugin.getChannelConfig().getStringList("channels." + plugin.getExactName(channelName) + "." + path) == null;
	}
	
	public void promote(Player player, String channelName) {
		assignAdmin(player, plugin.getExactName(channelName));
	}
	
	public void set(String channelName, String path, Object value) {
		plugin.getChannelConfig().set("channels." + plugin.getExactName(channelName) + "." + path, value);
		plugin.saveChannelConfig();
	}
	
	public void setChannelColour(String channelName, String colour) {
		plugin.getConfig().set("channels." + plugin.getExactName(channelName) + ".channel-display-colour", colour);
		plugin.saveConfig();
	}
	
	public void setConvertColours(String channelName, boolean convert) {
		plugin.getConfig().set("channels." + plugin.getExactName(channelName) + ".colour-code", convert);
		plugin.saveConfig();
	}
	
	public void setFormat(String channelName, String format) {
		plugin.getConfig().set("channels." + plugin.getExactName(channelName) + ".format", format);
		plugin.saveConfig();
	}
	
	public void setNameColour(String channelName, String colour) {
		plugin.getConfig().set("channels." + plugin.getExactName(channelName) + ".name-display-colour", colour);
		plugin.saveConfig();
	}
	
	public void setPassword(String channelName, String password) {
		plugin.getConfig().set("channels." + plugin.getExactName(channelName) + ".password", password);
		plugin.saveConfig();
	}
	
	public void setTag(String channelName, String tag) {
		plugin.getConfig().set("channels." + plugin.getExactName(channelName) + ".tag", tag);
		plugin.saveConfig();
	}
	
	public void setType(String channelName, String type) {
		plugin.getConfig().set("channels." + plugin.getExactName(channelName) + ".type", type);
		plugin.saveConfig();
	}
	
	public void unban(Player player, String channelName) {
		List<String> blacklist = (!isEmpty(plugin.getExactName(channelName), "blacklist")) ? get(plugin.getExactName(channelName), "blacklist") : new ArrayList<String>();
		blacklist.remove(player.getName());
		set(plugin.getExactName(channelName), "blacklist", blacklist);
		
		whitelistMember(player, plugin.getExactName(channelName));
	}
	
	public void unfollow(Player player, String channelName) {
		List<String> followerList = (!isEmpty(plugin.getExactName(channelName), "followers")) ? get(plugin.getExactName(channelName), "followers") : new ArrayList<String>();
		followerList.remove(player.getName());
		set(plugin.getExactName(channelName), "followers", followerList);
	}
	
	public void whitelistMember(Player player, String channelName) {
		List<String> whitelist = (!isEmpty(plugin.getExactName(channelName), "whitelist")) ? get(plugin.getExactName(channelName), "whitelist") : new ArrayList<String>();
		whitelist.add(player.getName());
		set(plugin.getExactName(channelName), "whitelist", whitelist);
	}
}