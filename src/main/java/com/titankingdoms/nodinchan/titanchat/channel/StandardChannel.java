package com.titankingdoms.nodinchan.titanchat.channel;

import org.bukkit.entity.Player;

/**
 * StandardChannel - Built-in channels
 * 
 * @author NodinChan
 *
 */
public final class StandardChannel extends Channel {
	
	private final ChannelVariables variables;
	
	private String password;
	
	public StandardChannel(String name) {
		this(name, Type.UNKNOWN);
	}
	
	public StandardChannel(String name, Type type) {
		super(name, type);
		this.variables = new ChannelVariables(this);
		this.password = "";
	}
	
	/**
	 * Check if the password is correct
	 * 
	 * @param password The password entered
	 * 
	 * @return True if password matches
	 */
	public boolean correctPassword(String password) {
		return this.password.equals(password);
	}
	
	/**
	 * Gets the password of the channel
	 * 
	 * @return The password if set
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Gets the variables of the channel
	 * 
	 * @return The channel variables
	 */
	public final ChannelVariables getVariables() {
		return variables;
	}
	
	@Override
	public void join(Player player) {
		super.getParticipants().add(player.getName());
		
		if (variables.enableJoinMessages() && plugin.enableJoinMessage()) {
			for (String participant : super.getParticipants()) {
				if (plugin.getPlayer(participant) != null && !plugin.getPlayer(participant).equals(player))
					plugin.sendInfo(plugin.getPlayer(participant), player.getDisplayName() + " has joined the channel");
			}
		}
	}
	
	@Override
	public void leave(Player player) {
		super.getParticipants().remove(player.getName());
		
		if (variables.enableLeaveMessages() && plugin.enableLeaveMessage()) {
			for (String participant : super.getParticipants()) {
				if (plugin.getPlayer(participant) != null)
					plugin.sendInfo(plugin.getPlayer(participant), player.getDisplayName() + " has left the channel");
			}
		}
	}
	
	@Override
	public void save() {
		getConfig().set("tag", variables.getTag());
		getConfig().set("chat-display-colour", variables.getChatColour());
		getConfig().set("name-display-colour", variables.getNameColour());
		getConfig().set("type", super.getType().getName());
		getConfig().set("global", super.isGlobal());
		getConfig().set("colour-code", variables.convert());
		getConfig().set("password", "");
		getConfig().set("format", variables.getFormat());
		getConfig().set("admins", super.getAdminList());
		getConfig().set("whitelist", super.getWhiteList());
		getConfig().set("blacklist", super.getBlackList());
		getConfig().set("followers", super.getFollowerList());
		saveConfig();
	}
	
	@Override
	public void sendMessage(Player player, String message) {
		if (super.isGlobal())
			plugin.getServer().broadcastMessage(message);
		
		else {
			for (String name : super.getParticipants()) {
				if (plugin.getPlayer(name) != null)
					plugin.getPlayer(name).sendMessage(message);
			}
			
			for (String name : super.getFollowerList()) {
				if (plugin.getPlayer(name) != null && !super.getParticipants().contains(name))
					plugin.getPlayer(name).sendMessage(message);
			}
		}
	}
	
	/**
	 * Sets the password of the channel
	 * 
	 * @param password The new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}