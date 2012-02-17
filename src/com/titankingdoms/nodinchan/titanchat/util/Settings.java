package com.titankingdoms.nodinchan.titanchat.util;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class Settings {
	
	private TitanChat plugin;
	
	public Settings(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public boolean enableJoinMessages() {
		return plugin.getConfig().getBoolean("channel-messages.join");
	}
	
	public boolean enableLeaveMessages() {
		return plugin.getConfig().getBoolean("channel-messages.leave");
	}
	
	public String getStatus(String channelName) {
		return plugin.getConfig().getString("channels." + channelName + ".status");
	}
}