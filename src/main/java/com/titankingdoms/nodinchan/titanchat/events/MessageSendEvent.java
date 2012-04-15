package com.titankingdoms.nodinchan.titanchat.events;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class MessageSendEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player sender;
	
	private String message;
	
	private boolean cancelled = false;
	
	private final List<Player> recipants;
	
	public MessageSendEvent(Player sender, List<Player> recipants, String message) {
		this.sender = sender;
		this.message = message;
		this.recipants = recipants;
	}
	
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
	
	public String getMessage() {
		return message;
	}
	
	public List<Player> getRecipants() {
		return recipants;
	}
	
	public Player getSender() {
		return sender;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}