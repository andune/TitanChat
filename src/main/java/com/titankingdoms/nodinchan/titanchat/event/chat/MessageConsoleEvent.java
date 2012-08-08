package com.titankingdoms.nodinchan.titanchat.event.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.titankingdoms.nodinchan.titanchat.event.util.Message;

public final class MessageConsoleEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player sender;
	
	private final Message message;
	
	public MessageConsoleEvent(Player sender, Message message) {
		this.sender = sender;
		this.message = message;
	}
	
	public String getFormat() {
		return message.getFormat();
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public String getMessage() {
		return message.getMessage();
	}
	
	public Player getSender() {
		return sender;
	}
	
	public void setFormat(String format) {
		this.message.setFormat(format);
	}
	
	public void setMessage(String message) {
		this.message.setMessage(message);
	}
}