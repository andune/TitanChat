package com.titankingdoms.nodinchan.titanchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class MessageSendEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player sender;
	
	private String message;
	
	public MessageSendEvent(Player sender, String message) {
		this.sender = sender;
		this.message = message;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Player getSender() {
		return sender;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}