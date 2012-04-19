package com.titankingdoms.nodinchan.titanchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MessageReceiveEvent extends Event implements Cancellable {
	
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