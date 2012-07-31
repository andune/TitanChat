package com.titankingdoms.nodinchan.titanchat.event.util;

public final class Message implements Cloneable {
	
	private String format;
	private String message;
	
	public Message(String format, String message) {
		this.format = format;
		this.message = message;
	}
	
	@Override
	public Message clone() {
		return new Message(format, message);
	}
	
	/**
	 * Gets the format
	 * 
	 * @return The format
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * Gets the message
	 * 
	 * @return The message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets the format
	 * 
	 * @param format The new format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * Sets the message
	 * 
	 * @param message The new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}