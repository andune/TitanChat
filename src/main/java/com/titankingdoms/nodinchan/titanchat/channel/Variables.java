package com.titankingdoms.nodinchan.titanchat.channel;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Variables - Basic Channel information storage
 * 
 * @author NodinChan
 *
 */
public final class Variables {

	private final TitanChat plugin;
	
	private final Channel channel;

	private String chatColour;
	private String format;
	private String nameColour;
	private String tag;
	
	private boolean convert;
	private boolean joinMessage;
	private boolean leaveMessage;
	
	/**
	 * Initialises variables
	 * 
	 * @param channel The Channel using this Variables instance
	 */
	public Variables(Channel channel) {
		this.plugin = TitanChat.getInstance();
		this.channel = channel;
		this.chatColour = "";
		this.format = "";
		this.nameColour = "";
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
	 * Gets the Channel that uses this ChannelVariable
	 * 
	 * @return The Channel using this instance
	 */
	public final Channel getChannel() {
		return channel;
	}
	
	/**
	 * Gets the chat colour
	 * 
	 * @return The chat colour
	 */
	public final String getChatColour() {
		return chatColour;
	}
	
	/**
	 * Gets the format
	 * 
	 * @return The format
	 */
	public final String getFormat() {
		if (!format.equals(""))
			return format;
		
		return plugin.getConfig().getString("formatting.format");
	}
	
	/**
	 * Gets the name colour
	 * 
	 * @return The name colour
	 */
	public final String getNameColour() {
		return nameColour;
	}
	
	/**
	 * Gets the tag
	 * 
	 * @return The tag
	 */
	public final String getTag() {
		return tag;
	}
	
	/**
	 * Sets the chat colour
	 * 
	 * @param chatColour The colour code to set to
	 */
	public final void setChatColour(String chatColour) {
		this.chatColour = chatColour;
	}
	
	/**
	 * Sets whether the Channel converts colour codes
	 * 
	 * @param convert True if converts
	 */
	public final void setConvert(boolean convert) {
		this.convert = convert;
	}
	
	/**
	 * Sets the format
	 * 
	 * @param format The format to set to
	 */
	public final void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * Sets the name colour
	 * 
	 * @param nameColour The colour code to set to
	 */
	public final void setNameColour(String nameColour) {
		this.nameColour = nameColour;
	}
	
	/**
	 * Sets the tag
	 * 
	 * @param tag The tag to set to
	 */
	public final void setTag(String tag) {
		this.tag = tag;
	}
}