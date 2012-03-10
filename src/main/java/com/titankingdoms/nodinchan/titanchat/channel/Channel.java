package com.titankingdoms.nodinchan.titanchat.channel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class Channel {
	
	protected TitanChat plugin;
	
	private String name;
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
	
	public Channel(TitanChat plugin, String channelName, Type type) {
		this.plugin = plugin;
		this.name = channelName;
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
	
	public List<String> getAdminList() {
		return adminlist;
	}
	
	public List<String> getBlackList() {
		return blacklist;
	}
	
	public List<String> getFollowers() {
		return followerlist;
	}
	
	public List<String> getInviteList() {
		return invitelist;
	}
	
	public List<String> getMuteList() {
		return mutelist;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Type getType() {
		return type;
	}
	
	public List<String> getWhiteList() {
		return whitelist;
	}
	
	public boolean isFollowing(Player player) {
		return followerlist.contains(player.getName());
	}
	
	public boolean isGlobal() {
		return global;
	}
	
	public boolean isSilenced() {
		return silenced;
	}
	
	public void join(Player player) {
		participants.add(player.getName());
		
		if (plugin.getConfigManager().enableJoinMessages()) {
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
		
		if (type.equals(Type.CUSTOM))
			plugin.getCustomChannel(this);
			
		else if (plugin.getConfigManager().enableLeaveMessages()) {
			for (String participant : participants) {
				if (plugin.getPlayer(participant) != null)
					plugin.sendInfo(plugin.getPlayer(participant), player.getDisplayName() + " has left the channel");
			}
		}
	}
	
	public void sendGlobalMessage(String message) {
		plugin.getServer().broadcastMessage(message);
	}
	
	public void sendMessage(String message) {
		for (String name : participants) {
			if (plugin.getPlayer(name) != null)
				plugin.getPlayer(name).sendMessage(message);
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
	
	public void unload() {
		plugin.getChannelConfig().set("channels." + name + ".admins", adminlist);
		plugin.getChannelConfig().set("channels." + name + ".blacklist", blacklist);
		plugin.getChannelConfig().set("channels." + name + ".whitelist", whitelist);
		plugin.getChannelConfig().set("channels." + name + ".followers", followerlist);
		plugin.saveChannelConfig();
	}
	
	public enum Type {
		CUSTOM("custom"),
		DEFAULT("default"),
		PASSWORD("password"),
		PRIVATE("private"),
		PUBLIC("public"),
		STAFF("staff"),
		UNKNOWN("unknown");
		
		private String name;
		
		private static final Map<String, Type> NAME_MAP = new HashMap<String, Type>();
		
		private Type(String name) {
			this.name = name;
		}
		
		static {
			for (Type type : EnumSet.allOf(Type.class)) {
				NAME_MAP.put(type.name, type);
			}
		}
		
		public static Type fromName(String name) {
			return NAME_MAP.get(name);
		}
		
		public String getName() {
			return name;
		}
	}
}