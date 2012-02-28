package com.titankingdoms.nodinchan.titanchat.channel;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class Channel {
	
	protected TitanChat plugin;
	
	private String name;
	private String password;
	
	private int radius;
	
	private Status status;
	
	private boolean global;
	private boolean silenced;
	
	private List<String> adminlist;
	private List<String> blacklist;
	private List<String> followerlist;
	private List<String> invitelist;
	private List<String> mutelist;
	private List<String> participants;
	private List<String> whitelist;
	
	public Channel(TitanChat plugin, String channelName) {
		this.plugin = plugin;
		this.name = channelName;
		this.password = "";
		this.radius = 0;
		this.status = Status.UNKNOWN;
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
		if (status.equals(Status.DEFAULT) || status.equals(Status.PUBLIC))
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
	
	public Status getStatus() {
		return status;
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
	
	public List<String> getParticipants() {
		return participants;
	}
	
	public void sendGlobalMessage(String message) {
		plugin.getServer().broadcastMessage(message);
	}

	public void sendLocalMessage(String name, String message) {
		if (plugin.getPlayer(name) != null) {
			List<Entity> entities = plugin.getPlayer(name).getNearbyEntities(radius, radius, radius);
			entities.add(plugin.getPlayer(name));
			
			for (Entity entity : entities) {
				if (entity instanceof Player)
					((Player) entity).sendMessage(message);
				else
					entities.remove(entity);
			}
			
			if (entities.size() == 1)
				plugin.getPlayer(name).sendMessage(ChatColor.GOLD + "Nobody hears you...");
		}
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
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public void setSilence(boolean silence) {
		this.silenced = silence;
	}
	
	public void setStatus(String status) {
		this.status = Status.fromName(status);
	}
	
	public enum Status {
		DEFAULT("default"),
		LOCAL("local"),
		PASSWORD("password"),
		PRIVATE("private"),
		PUBLIC("public"),
		STAFF("staff"),
		UNKNOWN("unknown");
		
		private String name;
		
		private static final Map<String, Status> NAME_MAP = new HashMap<String, Status>();
		
		private Status(String name) {
			this.name = name;
		}
		
		static {
			for (Status status : EnumSet.allOf(Status.class)) {
				NAME_MAP.put(status.name, status);
			}
		}
		
		public static Status fromName(String name) {
			return NAME_MAP.get(name);
		}
		
		public String getName() {
			return name;
		}
	}
}