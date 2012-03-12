package com.titankingdoms.nodinchan.titanchat.channel;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class ChannelVariables {
	
	private TitanChat plugin;
	
	private Channel channel;

	private String chatColour;
	private String format;
	private String nameColour;
	private String tag;
	
	private boolean convert;
	private boolean joinMessage;
	private boolean leaveMessage;
	
	public ChannelVariables(TitanChat plugin, Channel channel) {
		this.plugin = plugin;
		this.channel = channel;
		this.chatColour = "&f";
		this.format = "";
		this.nameColour = "&f";
		this.tag = "[]";
		this.convert = false;
	}
	
	public boolean convert() {
		return convert;
	}
	
	public boolean enableJoinMessages() {
		return joinMessage;
	}
	
	public boolean enableLeaveMessages() {
		return leaveMessage;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public String getChatColour() {
		return chatColour;
	}
	
	public String getFormat() {
		if (!format.equals(""))
			return format;
		
		return plugin.getConfig().getString("formatting.format");
	}
	
	public String getGroupPrefix(Player player) {
		return plugin.getGroupPrefix(player);
	}
	
	public String getGroupSuffix(Player player) {
		return plugin.getGroupSuffix(player);
	}
	
	public String getNameColour() {
		return nameColour;
	}
	
	public String getPlayerPrefix(Player player) {
		return plugin.getPlayerPrefix(player);
	}
	
	public String getPlayerSuffix(Player player) {
		return plugin.getPlayerSuffix(player);
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setChatColour(String chatColour) {
		this.chatColour = chatColour;
	}
	
	public void setConvert(boolean convert) {
		this.convert = convert;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public void setNameColour(String nameColour) {
		this.nameColour = nameColour;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}
}