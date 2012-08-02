package com.titankingdoms.nodinchan.titanchat.channel.util;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;

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

public class Info {
	
	protected final TitanChat plugin;
	
	private final Channel channel;
	
	public Info(Channel channel) {
		this.plugin = TitanChat.getInstance();
		this.channel = channel;
	}
	
	public boolean colouring() {
		return channel.getConfig().getBoolean("setting.colouring", false);
	}
	
	public boolean enableJoinMessage() {
		return channel.getConfig().getBoolean("messages.join", true);
	}
	
	public boolean enableLeaveMessage() {
		return channel.getConfig().getBoolean("messages.leave", true);
	}
	
	public String getChatColour() {
		return channel.getConfig().getString("chat-display-colour", "");
	}
	
	public String getFormat() {
		String format = channel.getConfig().getString("format", "");
		return (format.equals("")) ? plugin.getConfig().getString("formatting.format") : format;
	}
	
	public String getNameColour() {
		return channel.getConfig().getString("name-display-colour", "");
	}
	
	public String getTag() {
		return channel.getConfig().getString("tag", "");
	}
	
	public String getTopic() {
		return channel.getConfig().getString("topic", "");
	}
	
	public boolean global() {
		return channel.getConfig().getBoolean("setting.global");
	}
	
	public void setChatColour(String colour) {
		channel.getConfig().set("chat-display-colour", colour);
		channel.saveConfig();
	}
	
	public void setColouring(boolean colouring) {
		channel.getConfig().set("setting.colouring", colouring);
		channel.saveConfig();
	}
	
	public void setFormat(String format) {
		channel.getConfig().set("format", format);
		channel.saveConfig();
	}
	
	public void setGlobal(boolean global) {
		channel.getConfig().set("setting.global", global);
		channel.saveConfig();
	}
	
	public void setNameColour(String colour) {
		channel.getConfig().set("name-display-colour", colour);
		channel.saveConfig();
	}
	
	public void setTag(String tag) {
		channel.getConfig().set("tag", tag);
		channel.saveConfig();
	}
	
	public void setTopic(String topic) {
		channel.getConfig().set("topic", topic);
		channel.saveConfig();
	}
	
	public void setWhitelistOnly(boolean whitelistOnly) {
		channel.getConfig().set("setting.whitelist", whitelistOnly);
		channel.saveConfig();
	}
	
	public boolean whitelistOnly() {
		return channel.getConfig().getBoolean("setting.whitelist");
	}
}