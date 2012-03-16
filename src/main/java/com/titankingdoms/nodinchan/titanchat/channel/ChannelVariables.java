package com.titankingdoms.nodinchan.titanchat.channel;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

/**
 * ChannelVariables - Stores basic channel configuration
 * 
 * @author NodinChan
 *
 */
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
	
	public ChannelVariables(Channel channel) {
		this.plugin = TitanChat.getInstance();
		this.channel = channel;
		this.chatColour = "&f";
		this.format = "";
		this.nameColour = "&f";
		this.tag = "[]";
		this.convert = false;
	}
	
	/**
	 * Check if the channel converts colour codes
	 * 
	 * @return True if it converts colour codes
	 */
	public final boolean convert() {
		return convert;
	}
	
	/**
	 * Check if join messages are enabled
	 * 
	 * @return True if join messages are enabled
	 */
	public final boolean enableJoinMessages() {
		return joinMessage;
	}
	
	/**
	 * Check if leave messages are enabled
	 * 
	 * @return True if leave messages are enabled
	 */
	public final boolean enableLeaveMessages() {
		return leaveMessage;
	}
	
	/**
	 * Get the Channel that uses this ChannelVariable
	 * 
	 * @return The Channel using this instance
	 */
	public final Channel getChannel() {
		return channel;
	}
	
	/**
	 * Get the chat colour
	 * 
	 * @return The chat colour
	 */
	public final String getChatColour() {
		return chatColour;
	}
	
	/**
	 * Get the format
	 * 
	 * @return The format
	 */
	public final String getFormat() {
		if (!format.equals(""))
			return format;
		
		return plugin.getConfig().getString("formatting.format");
	}
	
	/**
	 * Get the group prefix of the Player
	 * 
	 * @param player The Player to check
	 * 
	 * @return The group prefix of the Player
	 */
	public final String getGroupPrefix(Player player) {
		return plugin.getGroupPrefix(player);
	}
	
	/**
	 * Get the group suffix of the Player
	 * 
	 * @param player The Player to check
	 * 
	 * @return The group suffix of the Player
	 */
	public final String getGroupSuffix(Player player) {
		return plugin.getGroupSuffix(player);
	}
	
	/**
	 * Get the name colour
	 * 
	 * @return The name colour
	 */
	public final String getNameColour() {
		return nameColour;
	}
	
	/**
	 * Get the prefix of the Player
	 * 
	 * @param player The Player to check
	 * 
	 * @return The prefix of the Player
	 */
	public final String getPlayerPrefix(Player player) {
		return plugin.getPlayerPrefix(player);
	}
	
	/**
	 * Get the suffix of the Player
	 * 
	 * @param player The Player to check
	 * 
	 * @return The suffix of the Player
	 */
	public final String getPlayerSuffix(Player player) {
		return plugin.getPlayerSuffix(player);
	}
	
	/**
	 * Get the tag
	 * 
	 * @return The tag
	 */
	public final String getTag() {
		return tag;
	}
	
	/**
	 * Set the chat colour
	 * 
	 * @param chatColour The colour code to set to
	 */
	public final void setChatColour(String chatColour) {
		this.chatColour = chatColour;
	}
	
	/**
	 * Set whether the Channel converts colour codes
	 * 
	 * @param convert True if converts
	 */
	public final void setConvert(boolean convert) {
		this.convert = convert;
	}
	
	/**
	 * Set the format
	 * 
	 * @param format The format to set to
	 */
	public final void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * Set the name colour
	 * 
	 * @param nameColour The colour code to set to
	 */
	public final void setNameColour(String nameColour) {
		this.nameColour = nameColour;
	}
	
	/**
	 * Set the tag
	 * 
	 * @param tag The tag to set to
	 */
	public final void setTag(String tag) {
		this.tag = tag;
	}
}