package com.titankingdoms.nodinchan.titanchat;

public class Settings {
	
	private TitanChat plugin;
	
	public Settings(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	// Check if channel join messages should be sent
	
	public boolean enableJoinMessages() {
		return plugin.getConfig().getBoolean("channel-messages.join");
	}
	
	// Check if channel leave messages should be sent
	
	public boolean enableLeaveMessages() {
		return plugin.getConfig().getBoolean("channel-messages.leave");
	}
	
	// Check if the channel is password protected
	
	public boolean isPassword(String channelName) {
		if (plugin.getStaffChannel().equals(channelName))
			return false;
		
		if (plugin.getDefaultChannel().equals(channelName))
			return false;
		
		if (plugin.getConfig().get("channels." + channelName + ".status") != null)
			return plugin.getConfig().getString("channels." + channelName + ".status").equalsIgnoreCase("password");
		
		return false;
	}
	
	// Check if the channel is private
	
	public boolean isPrivate(String channelName) {
		if (plugin.getStaffChannel().equals(channelName))
			return false;
		
		if (plugin.getDefaultChannel().equals(channelName))
			return true;
		
		if (plugin.getConfig().get("channels." + channelName + ".status") != null)
			return plugin.getConfig().getString("channels." + channelName + ".status").equalsIgnoreCase("private");
		
		return false;
	}
	
	// Check if the channel is public
	
	public boolean isPublic(String channelName) {
		if (plugin.getStaffChannel().equals(channelName))
			return false;
		
		if (plugin.getDefaultChannel().equals(channelName))
			return true;
		
		if (plugin.getConfig().get("channels." + channelName + ".status") != null)
			return plugin.getConfig().getString("channels." + channelName + ".status").equalsIgnoreCase("public");
		
		return false;
	}
}
