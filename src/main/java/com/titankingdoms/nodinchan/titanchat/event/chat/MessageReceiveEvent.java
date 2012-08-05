package com.titankingdoms.nodinchan.titanchat.event.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.titankingdoms.nodinchan.titanchat.event.util.Message;


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
 * MessageReceiveEvent - Called when Players will receive the message
 * 
 * @author NodinChan
 *
 */
public final class MessageReceiveEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player sender;
	
	private final Message message;
	
	private final Map<Player, Message> messages;
	private final Map<Player, Boolean> cancelled;
	
	/**
	 * Called when Players will receive the message
	 * 
	 * @param sender The message sender
	 * 
	 * @param recipants The message recipants
	 * 
	 * @param message The message
	 */
	public MessageReceiveEvent(Player sender, List<Player> recipants, Message message) {
		this.sender = sender;
		this.message = message;
		this.messages = new HashMap<Player, Message>();
		this.cancelled = new HashMap<Player, Boolean>();
		
		for (Player recipant : recipants) {
			messages.put(recipant, message.clone());
			cancelled.put(recipant, false);
		}
	}
	
	/**
	 * Called when Players will receive the message
	 * 
	 * @param sender The message sender
	 * 
	 * @param recipants The message recipants
	 * 
	 * @param message The message
	 */
	public MessageReceiveEvent(Player sender, Player[] recipants, Message message) {
		this(sender, Arrays.asList(recipants), message);
	}
	
	/**
	 * Gets the format to be used for the player
	 * 
	 * @param recipant The recipant of the message
	 * 
	 * @return The format to be used
	 */
	public String getFormat(Player recipant) {
		if (messages.containsKey(recipant))
			return messages.get(recipant).getFormat();
		
		return message.getFormat();
	}
	
	/**
	 * Gets the entire formatted message to be sent to the player
	 * 
	 * @param recipant The recipant of the message
	 * 
	 * @return The formatted message
	 */
	public String getFormattedMessage(Player recipant) {
		String format = getFormat(recipant);
		String message = getMessage(recipant);
		
		return format.replace("%message", message);
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	/**
	 * Gets the message to be sent to the player
	 * 
	 * @param recipant The recipant of the message
	 * 
	 * @return The message to be sent
	 */
	public String getMessage(Player recipant) {
		if (messages.containsKey(recipant))
			return messages.get(recipant).getMessage();
		
		return message.getMessage();
	}
	
	/**
	 * Gets the recipants of the message
	 * 
	 * @return The list of recipants
	 */
	public List<Player> getRecipants() {
		List<Player> recipants = new ArrayList<Player>(messages.keySet());
		
		for (Player recipant : messages.keySet())
			if (cancelled.containsKey(recipant) && cancelled.get(recipant))
				recipants.add(recipant);
		
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
	 * Check if the event should be cancelled for the player
	 * 
	 * @param recipant The message recipant
	 * 
	 * @return True if cancelled
	 */
	public boolean isCancelled(Player recipant) {
		if (cancelled.containsKey(recipant))
			return cancelled.get(recipant);
		
		return true;
	}
	
	/**
	 * Cancels the event for the player
	 * 
	 * @param recipant The message recipant
	 * 
	 * @param cancelled Set to true to cancel
	 */
	public void setCancelled(Player recipant, boolean cancelled) {
		this.cancelled.put(recipant, cancelled);
	}
	
	/**
	 * Sets the format to be used for the player
	 * 
	 * @param recipant The message recipant
	 * 
	 * @param format The new format
	 */
	public void setFormat(Player recipant, String format) {
		if (messages.containsKey(recipant))
			messages.get(recipant).setFormat(format);
	}
	
	/**
	 * Sets the message to be sent to the player
	 * 
	 * @param recipant The message recipant
	 * 
	 * @param message The new message
	 */
	public void setMessage(Player recipant, String message) {
		if (messages.containsKey(recipant))
			messages.get(recipant).setMessage(message);
	}
}