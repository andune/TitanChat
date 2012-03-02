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
	
	public void createChannel(Player player, String channelName) {
		List<String> adminList = (!isEmpty(plugin.getExactName(channelName), "admins")) ? get(plugin.getExactName(channelName), "admins") : new ArrayList<String>();
		adminList.add(player.getName());
		set(plugin.getExactName(channelName), "admins", adminList);
		
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
	
	public boolean enableJoinMessages() {
		return plugin.getConfig().getBoolean("channel-messages.join");
	}
	
	public boolean enableLeaveMessages() {
		return plugin.getConfig().getBoolean("channel-messages.leave");
	}
	
	public List<String> get(String channelName, String path) {
		return plugin.getChannelConfig().getStringList("channels." + plugin.getExactName(channelName) + "." + path);
	}
	
	public boolean isEmpty(String channelName, String path) {
		return plugin.getChannelConfig().getStringList("channels." + plugin.getExactName(channelName) + "." + path) == null;
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
}