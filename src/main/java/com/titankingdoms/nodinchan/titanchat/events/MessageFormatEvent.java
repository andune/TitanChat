package com.titankingdoms.nodinchan.titanchat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class MessageFormatEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player sender;
	
	private String format;
	
	public MessageFormatEvent(Player sender, String format) {
		this.sender = sender;
		this.format = format;
	}
	
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
	
	public Player getSender() {
		return sender;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
}