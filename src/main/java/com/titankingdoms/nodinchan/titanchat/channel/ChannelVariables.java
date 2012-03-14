package com.titankingdoms.nodinchan.titanchat.channel;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class ChannelVariables {
	
	private final TitanChat plugin;
	
	private final Channel channel;

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
	
	public final boolean convert() {
		return convert;
	}
	
	public final boolean enableJoinMessages() {
		return joinMessage;
	}
	
	public final boolean enableLeaveMessages() {
		return leaveMessage;
	}
	
	public final Channel getChannel() {
		return channel;
	}
	
	public final String getChatColour() {
		return chatColour;
	}
	
	public final String getFormat() {
		if (!format.equals(""))
			return format;
		
		return plugin.getConfig().getString("formatting.format");
	}
	
	public final String getGroupPrefix(Player player) {
		return plugin.getGroupPrefix(player);
	}
	
	public final String getGroupSuffix(Player player) {
		return plugin.getGroupSuffix(player);
	}
	
	public final String getNameColour() {
		return nameColour;
	}
	
	public final String getPlayerPrefix(Player player) {
		return plugin.getPlayerPrefix(player);
	}
	
	public final String getPlayerSuffix(Player player) {
		return plugin.getPlayerSuffix(player);
	}
	
	public final String getTag() {
		return tag;
	}
	
	public final void setChatColour(String chatColour) {
		this.chatColour = chatColour;
	}
	
	public final void setConvert(boolean convert) {
		this.convert = convert;
	}
	
	public final void setFormat(String format) {
		this.format = format;
	}
	
	public final void setNameColour(String nameColour) {
		this.nameColour = nameColour;
	}
	
	public final void setTag(String tag) {
		this.tag = tag;
	}
}