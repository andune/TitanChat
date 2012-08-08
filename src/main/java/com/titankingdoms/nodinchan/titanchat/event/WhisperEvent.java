package com.titankingdoms.nodinchan.titanchat.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.titankingdoms.nodinchan.titanchat.event.util.Message;

public final class WhisperEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final CommandSender sender;
	private final CommandSender recipant;
	
	private final Message message;
	
	public WhisperEvent(CommandSender sender, CommandSender recipant, Message message) {
		this.sender = sender;
		this.recipant = recipant;
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
	
	public CommandSender getRecipant() {
		return recipant;
	}
	
	public CommandSender getSender() {
		return sender;
	}
	
	public void setFormat(String format) {
		this.message.setFormat(format);
	}
	
	public void setMessage(String message) {
		this.message.setMessage(message);
	}
}