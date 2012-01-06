package com.titankingdoms.nodinchan.titanchat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TitanChat extends JavaPlugin {
	
	private static Logger log = Logger.getLogger("TitanLog");
	
	private File channelConfigFile = new File(getDataFolder(), "channels.yml");
	private FileConfiguration channelConfig = YamlConfiguration.loadConfiguration(channelConfigFile);
	
	private Map<String, List<Player>> channelAdmins = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> channelMembers = new HashMap<String, List<Player>>();
	private Map<Player, String> channel = new HashMap<Player, String>();
	private Map<String, List<Player>> participants = new HashMap<String, List<Player>>();
	
	public void assignAdmin(Player player, String channelName) {
		List<Player> admins = channelAdmins.get(channelName);
		admins.add(player);
		channelAdmins.put(channelName, admins);
	}
	
	public boolean channelExist(String channelName) {
		return (getConfig().getConfigurationSection("").getKeys(false).contains(channelName));
	}
	
	public void channelSwitch(Player player, String channelName) {
		leaveChannel(player, channelName);
		enterChannel(player, channelName);
	}
	
	public void enterChannel(Player player, String channelName) {
		channel.put(player, channelName);
		List<Player> players = participants.get(channelName);
		players.add(player);
		participants.put(channelName, players);
	}
	
	public String getChannel(Player player) {
		return channel.get(player);
	}
	
	public List<Player> getChannelAdmins(String channelName) {
		return channelAdmins.get(channelName);
	}
	
	public FileConfiguration getChannelConfig() {
		return channelConfig;
	}
	
	public List<Player> getParticipants(String channelName) {
		return participants.get(channelName);
	}
	
	public void infoLog(String info) {
		log.info("[" + this + "] " + info);
	}
	
	public boolean isAdmin(Player player) {
		if (channelAdmins.get(getChannel(player)).contains(player))
			return true;
		
		else if (getChannelConfig().getStringList("channels." + getChannel(player) + ".admins").contains(player.getName()))
			return true;
		
		else
			return false;
	}
	
	public boolean canJoinFreely(String channelName) {
		return (getConfig().getBoolean("channels." + channelName + ".free-to-join"));
	}
	
	public boolean isMember(Player player) {
		if (channelMembers.get(getChannel(player)).contains(player))
			return true;
		
		else if (getChannelConfig().getStringList("channels." + getChannel(player) + ".members").contains(player.getName()))
			return true;
		
		else
			return false;
	}
	
	public void leaveChannel(Player player, String channelName) {
		channel.remove(player);
		List<Player> players = participants.get(channelName);
		players.remove(player);
		participants.put(channelName, players);
	}
	
	@Override
	public void onDisable() {
		infoLog("is now disabled");
	}
	
	@Override
	public void onEnable() {
		infoLog("is now enabling...");
		infoLog("is now enabled");
	}
	
	public void sendWarning(Player player, String warning) {
		player.sendMessage("[TitanChat] " + ChatColor.RED + warning);
	}
}
