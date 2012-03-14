package com.titankingdoms.nodinchan.titanchat.channel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class Channel {

	protected final TitanChat plugin;
	
	private ChannelVariables variables;
	
	private final String name;
	private String password;
	
	private Type type;
	
	private boolean global;
	private boolean silenced;
	
	private List<String> adminlist;
	private List<String> blacklist;
	private List<String> followerlist;
	private List<String> invitelist;
	private List<String> mutelist;
	private List<String> participants;
	private List<String> whitelist;
	
	private File configFile = null;
	private FileConfiguration config = null;
	
	public Channel(TitanChat plugin, String name) {
		this(plugin, name, Type.UNKNOWN);
	}
	
	public Channel(TitanChat plugin, String name, Type type) {
		this.plugin = plugin;
		this.variables = new ChannelVariables(plugin, this);
		this.name = name;
		this.password = "";
		this.type = type;
		this.global = false;
		this.silenced = false;
		this.adminlist = new ArrayList<String>();
		this.blacklist = new ArrayList<String>();
		this.followerlist = new ArrayList<String>();
		this.invitelist = new ArrayList<String>();
		this.mutelist = new ArrayList<String>();
		this.participants = new ArrayList<String>();
		this.whitelist = new ArrayList<String>();
	}
	
	public Channel(TitanChat plugin, String name, ChannelVariables variables) {
		this.plugin = plugin;
		this.variables = variables;
		this.name = name;
		this.password = "";
		this.type = Type.CUSTOM;
		this.global = false;
		this.silenced = false;
		this.adminlist = new ArrayList<String>();
		this.blacklist = new ArrayList<String>();
		this.followerlist = new ArrayList<String>();
		this.invitelist = new ArrayList<String>();
		this.mutelist = new ArrayList<String>();
		this.participants = new ArrayList<String>();
		this.whitelist = new ArrayList<String>();
	}
	
	public boolean canAccess(Player player) {
		if (plugin.has(player, "TitanChat.access.*") || plugin.has(player, "TitanChat.access." + name))
			return true;
		if (blacklist.contains(player.getName()))
			return false;
		if (type.equals(Type.DEFAULT) || type.equals(Type.PUBLIC))
			return true;
		if (adminlist.contains(player.getName()) || whitelist.contains(player.getName()))
			return true;
		
		return false;
	}
	
	public boolean canBan(Player player) {
		if (plugin.has(player, "TitanChat.ban.*") || plugin.has(player, "TitanChat.ban." + name))
			return true;
		if (adminlist.contains(player.getName()))
			return true;
		
		return false;
	}
	
	public boolean canKick(Player player) {
		if (plugin.has(player, "TitanChat.kick.*") || plugin.has(player, "TitanChat.kick." + name))
			return true;
		if (adminlist.contains(player.getName()))
			return true;
		
		return false;
	}
	
	public boolean canMute(Player player) {
		if (plugin.has(player, "TitanChat.silence") || plugin.has(player, "TitanChat.mute"))
			return true;
		if (adminlist.contains(player.getName()))
			return true;
		
		return false;
	}
	
	public boolean canRank(Player player) {
		if (plugin.has(player, "TitanChat.rank.*") || plugin.has(player, "TitanChat.rank." + name))
			return true;
		if (adminlist.contains(player.getName()))
			return true;
		
		return false;
	}
	
	public boolean correctPassword(String password) {
		return this.password.equals(password);
	}
	
	@Override
	public final boolean equals(Object object) {
		if (object instanceof Channel)
			return ((Channel) object).getName().equals(getName());
		
		return false;
	}
	
	public List<String> getAdminList() {
		return adminlist;
	}
	
	public List<String> getBlackList() {
		return blacklist;
	}
	
	public final FileConfiguration getConfig() {
		if (config == null) { reloadConfig(); }
		return config;
	}
	
	public List<String> getFollowerList() {
		return followerlist;
	}
	
	public List<String> getInviteList() {
		return invitelist;
	}
	
	public List<String> getMuteList() {
		return mutelist;
	}
	
	public final String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public final Type getType() {
		return type;
	}
	
	public final ChannelVariables getVariables() {
		return variables;
	}
	
	public List<String> getWhiteList() {
		return whitelist;
	}
	
	public boolean isGlobal() {
		return global;
	}
	
	public boolean isSilenced() {
		return silenced;
	}
	
	public void join(Player player) {
		participants.add(player.getName());
		
		if (variables.enableJoinMessages() && plugin.enableJoinMessage()) {
			for (String participant : participants) {
				if (plugin.getPlayer(participant) != null && !plugin.getPlayer(participant).equals(player))
					plugin.sendInfo(plugin.getPlayer(participant), player.getDisplayName() + " has joined the channel");
			}
		}
	}
	
	public List<String> getParticipants() {
		return participants;
	}
	
	public void leave(Player player) {
		participants.remove(player.getName());
		
		if (variables.enableLeaveMessages() && plugin.enableLeaveMessage()) {
			for (String participant : participants) {
				if (plugin.getPlayer(participant) != null)
					plugin.sendInfo(plugin.getPlayer(participant), player.getDisplayName() + " has left the channel");
			}
		}
	}
	
	public final void reloadConfig() {
		if (configFile == null) { configFile = new File(plugin.getChannelDir(), name + ".yml"); }
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = plugin.getResource("democonfig.yml");
		
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	public void save() {
		getConfig().set("tag", variables.getTag());
		getConfig().set("chat-display-colour", variables.getChatColour());
		getConfig().set("name-display-colour", variables.getNameColour());
		getConfig().set("type", type.getName());
		getConfig().set("global", global);
		getConfig().set("colour-code", variables.convert());
		getConfig().set("password", "");
		getConfig().set("format", variables.getFormat());
		getConfig().set("admins", adminlist);
		getConfig().set("whitelist", whitelist);
		getConfig().set("blacklist", blacklist);
		getConfig().set("followers", followerlist);
		saveConfig();
	}
	
	public final void saveConfig() {
		if (configFile == null || config == null) { return; }
		try { config.save(configFile); } catch (IOException e) { plugin.log(Level.SEVERE, "Could not save config to " + configFile); }
	}
	
	public void sendMessage(String message) {
		if (global)
			plugin.getServer().broadcastMessage(message);
		
		else {
			for (String name : participants) {
				if (plugin.getPlayer(name) != null)
					plugin.getPlayer(name).sendMessage(message);
			}
			
			for (String name : followerlist) {
				if (plugin.getPlayer(name) != null && !participants.contains(name))
					plugin.getPlayer(name).sendMessage(message);
			}
		}
	}
	
	public void setGlobal(boolean global) {
		this.global = global;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setSilenced(boolean silenced) {
		this.silenced = silenced;
	}
	
	public void setType(String type) {
		this.type = Type.fromName(type);
	}
}