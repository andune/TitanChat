package com.titankingdoms.nodinchan.titanchat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class TitanChat extends JavaPlugin {
	
	private static Logger log = Logger.getLogger("TitanLog");
	
	private String defaultChannel = "";
	
	private File channelConfigFile = null;
	private FileConfiguration channelConfig = null;
	
	private Map<String, List<Player>> channelAdmins = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> channelMembers = new HashMap<String, List<Player>>();
	private Map<Player, String> channel = new HashMap<Player, String>();
	private Map<Player, List<String>> invitations = new HashMap<Player, List<String>>();
	private Map<String, List<Player>> invited = new HashMap<String, List<Player>>();
	private Map<String, List<Player>> participants = new HashMap<String, List<Player>>();
	
	// Accept the channel join request
	
	public void accept(Player player, String newCh) {
		String channel = getChannel(player);
		channelSwitch(player, channel, newCh);
		List<Player> players = invited.get(newCh);
		players.remove(player);
		invited.put(newCh, players);
		List<String> channels = invitations.get(player);
		channels.remove(newCh);
		invitations.put(player, channels);
	}
	
	// Assigning an Admin to a channel
	
	public void assignAdmin(Player player, String channelName) {
		List<Player> admins = channelAdmins.get(channelName);
		admins.add(player);
		channelAdmins.put(channelName, admins);
	}
	
	// Banning a Member from a channel
	
	public void ban(Player player, String channelName) {
		if (isAdmin(player)) {
			List<Player> admins = channelAdmins.get(channelName);
			admins.remove(player);
			channelAdmins.put(channelName, admins);
			
		} else if (isMember(player)) {
			List<Player> members = channelMembers.get(channelName);
			members.remove(player);
			channelMembers.put(channelName, members);
		}
		
		channelSwitch(player, getChannel(player), getDefaultChannel());
		sendWarning(player, "You have been banned from " + channelName);
	}
	
	// Check if a channel exists
	
	public boolean channelExist(String channelName) {
		return (getConfig().getConfigurationSection("channels").getKeys(false).contains(channelName));
	}
	
	// Switching channels
	
	public void channelSwitch(Player player, String oldCh, String newCh) {
		leaveChannel(player, oldCh);
		enterChannel(player, newCh);
	}
	
	// Creating channels
	
	public void createChannel(Player player, String channelName) {
		assignAdmin(player, channelName);
		channelSwitch(player, getChannel(player), channelName);
	}
	
	public String createList(List<String> channels) {
		StringBuilder str = new StringBuilder();
		
		for (String item : channels) {
			if (str.length() > 0) {
				str.append(", ");
			}
			
			str.append(item);
		}
		
		return str.toString();
	}
	
	// Declining a channel join request
	
	public void decline(Player player, String newCh) {
		List<Player> players = invited.get(newCh);
		players.remove(player);
		invited.put(newCh, players);
		List<String> channels = invitations.get(player);
		channels.remove(newCh);
		invitations.put(player, channels);
	}
	
	// Demoting a player on a channel
	
	public void demote(Player player, String channelName) {
		List<Player> admins = channelAdmins.get(channelName);
		admins.remove(player);
		channelAdmins.put(channelName, admins);
		whitelistMember(player, channelName);
	}
	
	// Entering channels
	
	public void enterChannel(Player player, String channelName) {
		channel.put(player, channelName);
		List<Player> players = participants.get(channelName);
		players.add(player);
		participants.put(channelName, players);
	}
	
	// Gets the channel the player is in
	
	public String getChannel(Player player) {
		return channel.get(player);
	}
	
	// Gets the admins of the channel
	
	public List<Player> getChannelAdmins(String channelName) {
		return channelAdmins.get(channelName);
	}
	
	// Gets the player config of channels
	
	public FileConfiguration getChannelConfig() {
		if (channelConfig == null) {
			reloadChannelConfig();
		}
		
		return channelConfig;
	}
	
	// Gets the name of the default channel
	
	public String getDefaultChannel() {
		if (defaultChannel == null) {
			for (String channel : getConfig().getConfigurationSection("channels").getKeys(false)) {
				if (getConfig().get("channels." + channel + ".default") != null) {
					defaultChannel = channel;
				}
			}
		}
		return defaultChannel;
	}
	
	// Gets the participants of a channel
	
	public List<Player> getParticipants(String channelName) {
		return participants.get(channelName);
	}
	
	public Player getPlayer(String name) {
		return getServer().getPlayer(name);
	}
	
	public void infoLog(String info) {
		log.info("[" + this + "] " + info);
	}
	
	// Inviting a player to join a channel
	
	public void invite(Player player, String channelName) {
		List<Player> players = invited.get(channelName);
		players.add(player);
		invited.put(channelName, players);
		List<String> channels = invitations.get(player);
		channels.add(channelName);
		invitations.put(player, channels);
	}
	
	// Check if the player is an admin of that channel
	
	public boolean isAdmin(Player player) {
		if (channelAdmins.get(getChannel(player)).contains(player))
			return true;
		
		else if (getChannelConfig().getStringList("channels." + getChannel(player) + ".admins").contains(player.getName()))
			return true;
		
		else if (player.hasPermission("TitanChat.admin"))
			return true;
		
		else
			return false;
	}
	
	public boolean isAdmin(Player player, String channelName) {
		if (channelAdmins.get(channelName).contains(player))
			return true;
		
		else if (getChannelConfig().getStringList("channels." + channelName + ".admins").contains(player.getName()))
			return true;
		
		else if (player.hasPermission("TitanChat.admin"))
			return true;
		
		else
			return false;
	}
	
	// Check if the player is invited
	
	public boolean isInvited(Player player, String channel) {
		if (invited.get(channel).contains(player))
			return true;
		
		else
			return false;
	}
	
	// Check if the channel is public
	
	public boolean isPublic(String channelName) {
		if (getConfig().getBoolean("channels." + channelName + ".public"))
			return true;
		
		else if (channelName == getDefaultChannel())
			return true;
		
		else
			return false;
	}
	
	// Check if the player is a member of that channel
	
	public boolean isMember(Player player) {
		if (channelMembers.get(getChannel(player)).contains(player))
			return true;
		
		else if (getChannelConfig().getStringList("channels." + getChannel(player) + ".members").contains(player.getName()))
			return true;
		
		else
			return false;
	}
	
	public boolean isMember(Player player, String channelName) {
		if (channelMembers.get(channelName).contains(player))
			return true;
		
		else if (getChannelConfig().getStringList("channels." + channelName + ".members").contains(player.getName()))
			return true;
		
		else
			return false;
	}
	
	// Kicks a player from a channel
	
	public void kick(Player player, String channelName) {
		channelSwitch(player, channelName, getDefaultChannel());
		sendWarning(player, "You have been kicked from " + channelName);
	}
	
	// Leaves the channel
	
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
		
		if (getDefaultChannel() == null) {
			log.warning("[" + this + "] Default channel not defined");
		}
		
		if (getConfig() == null) {
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
		if (getChannelConfig() == null) {
			getChannelConfig().options().copyDefaults(true);
			saveChannelConfig();
		}
		
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Type.PLAYER_CHAT, new TitanChatPlayerListener(this), Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_JOIN, new TitanChatPlayerListener(this), Priority.Highest, this);
		pm.registerEvent(Type.PLAYER_QUIT, new TitanChatPlayerListener(this), Priority.Highest, this);
		
		getCommand("titanchat").setExecutor(new TitanChatCommands(this));
		
		infoLog("is now enabled");
	}
	
	// Promote a player on a channel
	
	public void promote(Player player, String channelName) {
		List<Player> members = channelMembers.get(channelName);
		members.remove(player);
		assignAdmin(player, channelName);
	}
	
	// Reloads the player config of the channels
	
	public void reloadChannelConfig() {
		if (channelConfigFile == null) {
			channelConfigFile = new File(getDataFolder(), "channels.yml");
		}
		
		channelConfig = YamlConfiguration.loadConfiguration(channelConfigFile);
		
		InputStream defConfigStream = getResource("channels.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			channelConfig.setDefaults(defConfig);
		}
	}
	
	// Saves the player config of the channels
	
	public void saveChannelConfig() {
		try {
			channelConfig.save(channelConfigFile);
		} catch (IOException e) {
			log.severe("Could not save config to " + channelConfigFile);
		}
	}
	
	public void sendInfo(Player player, String info) {
		player.sendMessage("[TitanChat] " + ChatColor.GOLD + info);
	}
	
	public void sendWarning(Player player, String warning) {
		player.sendMessage("[TitanChat] " + ChatColor.RED + warning);
	}
	
	// Whitelisting a Member on a channel
	
	public void whitelistMember(Player player, String channelName) {
		List<Player> members = channelMembers.get(channelName);
		members.add(player);
		channelMembers.put(channelName, members);
	}
}
