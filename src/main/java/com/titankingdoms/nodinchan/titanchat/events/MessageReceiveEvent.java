package com.titankingdoms.nodinchan.titanchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
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
 * MessageReceiveEvent - Called when a Player is to recieve a message
 * 
 * @author NodinChan
 *
 */
public final class MessageReceiveEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player sender;
	private final Player recipant;
	
	private String message;
	
	private boolean cancelled = false;
	
	/**
	 * Called when a Player is to recieve a message
	 * 
	 * @param sender The message sender
	 * 
	 * @param recipant The message recipant
	 * 
	 * @param message The message
	 */
	public MessageReceiveEvent(Player sender, Player recipant, String message) {
		this.sender = sender;
		this.recipant = recipant;
		this.message = message;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	/**
	 * Gets the message
	 * 
	 * @return The message to be sent
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Gets the message recipant
	 * 
	 * @return The recipant of the message
	 */
	public Player getRecipant() {
		return recipant;
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
	 * Check if the event is canclled
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
	/**
	 * Sets the message
	 * 
	 * @param message The new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Sets the event as cancelled
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}