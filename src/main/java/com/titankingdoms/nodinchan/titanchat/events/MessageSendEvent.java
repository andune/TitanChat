package com.titankingdoms.nodinchan.titanchat.events;

import java.util.Arrays;
import java.util.List;

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
 * MessageSendEvent - Called when a message is to be sent to a group of Players
 * 
 * @author NodinChan
 *
 */
public final class MessageSendEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player sender;
	
	private String message;
	
	private boolean cancelled = false;
	
	private final List<Player> recipants;
	
	/**
	 * Called when a message is to be sent to a group of Players
	 * 
	 * @param sender The message sender
	 * 
	 * @param recipants The message recipants
	 * 
	 * @param message The message
	 */
	public MessageSendEvent(Player sender, List<Player> recipants, String message) {
		this.sender = sender;
		this.message = message;
		this.recipants = recipants;
	}
	
	/**
	 * Called when a message is going to be sent to a group of Players
	 * 
	 * @param sender The message sender
	 * 
	 * @param recipants The message recipants
	 * 
	 * @param message The message
	 */
	public MessageSendEvent(Player sender, Player[] recipants, String message) {
		this(sender, Arrays.asList(recipants), message);
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
	 * Gets the message recipants
	 * 
	 * @return The recipants of the message
	 */
	public List<Player> getRecipants() {
		return recipants;
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