package com.titankingdoms.nodinchan.titanchat.channel.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;

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
		
		private final int minArgs;
		private final int maxArgs;
		
		public HandlerInfo(String description, String usage, int minArgs, int maxArgs) {
			this.description = description;
			this.usage = usage;
			this.minArgs = minArgs;
			this.maxArgs = maxArgs;
		}
		
		public String getDescription() {
			return description;
		}
		
		public int getMaxArgs() {
			return maxArgs;
		}
		
		public int getMinArgs() {
			return minArgs;
		}
		
		public String getUsage() {
			return usage;
		}
	}
}