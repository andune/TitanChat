package com.titankingdoms.nodinchan.titanchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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
 * MessageFormatEvent - Called when a message formats
 * 
 * @author NodinChan
 *
 */
public final class MessageFormatEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player sender;
	
	private String format;
	
	/**
	 * Called when a message formats
	 * 
	 * @param sender The message sender
	 * 
	 * @param format The format
	 */
	public MessageFormatEvent(Player sender, String format) {
		this.sender = sender;
		this.format = format;
	}
	
	/**
	 * Gets the format
	 * 
	 * @return The format of the message
	 */
	public String getFormat() {
		return format;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	/**
	 * Gets the message sender
	 * 
	 * @return The sender of the message
	 */
	public Player getSender() {
		return sender;
	}
	
	/**
	 * Sets the format
	 * 
	 * @param format The new format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
}