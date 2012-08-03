package com.titankingdoms.nodinchan.titanchat.channel.util.handler;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

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

public final class Handler {
	
	private final Map<String, CommandHandler> commandHandlers;
	private final Map<String, SettingHandler> settingHandlers;
	
	public Handler() {
		this.commandHandlers = new HashMap<String, CommandHandler>();
		this.settingHandlers = new HashMap<String, SettingHandler>();
	}
	
	public boolean changeSetting(CommandSender sender, String setting, String[] args) {
		if (settingHandlers.containsKey(setting.toLowerCase())) {
			settingHandlers.get(setting.toLowerCase()).set(sender, args);
			return true;
		}
		
		return false;
	}
	
	public boolean handleCommand(CommandSender sender, String command, String[] args) {
		if (commandHandlers.containsKey(command.toLowerCase())) {
			commandHandlers.get(command.toLowerCase()).onCommand(sender, args);
			return true;
		}
		
		return false;
	}
	
	public void registerCommandHandlers(CommandHandler... handlers) {
		for (CommandHandler handler : handlers)
			if (!commandHandlers.containsKey(handler.getCommand()))
				commandHandlers.put(handler.getCommand().toLowerCase(), handler);
	}
	
	public void registerSettingHandlers(SettingHandler... handlers) {
		for (SettingHandler handler : handlers)
			if (!settingHandlers.containsKey(handler.getSetting()))
				settingHandlers.put(handler.getSetting().toLowerCase(), handler);
	}
	
	public static final class HandlerInfo {
		
		private final String description;
		private final String usage;
		
		public HandlerInfo(String description, String usage) {
			this.description = description;
			this.usage = usage;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getUsage() {
			return usage;
		}
	}
}