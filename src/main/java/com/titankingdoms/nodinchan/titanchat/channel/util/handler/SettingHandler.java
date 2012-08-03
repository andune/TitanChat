package com.titankingdoms.nodinchan.titanchat.channel.util.handler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.util.handler.Handler.HandlerInfo;

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

public abstract class SettingHandler {
	
	protected final TitanChat plugin;
	
	protected final Channel channel;
	
	private final String setting;
	
	private final HandlerInfo info;
	
	public SettingHandler(Channel channel, String setting, HandlerInfo info) {
		this.plugin = TitanChat.getInstance();
		this.channel = channel;
		this.setting = setting;
		this.info = info;
	}
	
	public final HandlerInfo getInfo() {
		return info;
	}
	
	public final String getSetting() {
		return setting;
	}
	
	public final boolean hasPermission(CommandSender sender, String permission) {
		return hasPermission(sender, permission, false);
	}
	
	public final boolean hasPermission(CommandSender sender, String permission, boolean avoidWildcard) {
		if (!(sender instanceof Player))
			return true;
		
		return plugin.getPermsBridge().has((Player) sender, permission, avoidWildcard);
	}
	
	public final void invalidArgLength(CommandSender sender) {
		plugin.send(MessageLevel.WARNING, sender, "Invalid Argument Length");
		usage(sender);
	}
	
	public abstract void set(CommandSender sender, String[] args);
	
	public final void usage(CommandSender sender) {
		if (info.getUsage().isEmpty())
			plugin.send(MessageLevel.WARNING, sender, "Usage: /titanchat <@><channel> set [setting] <arguments>");
		else
			plugin.send(MessageLevel.WARNING, sender, "Usage: /titanchat <@><channel> set " + info.getUsage());
	}
}