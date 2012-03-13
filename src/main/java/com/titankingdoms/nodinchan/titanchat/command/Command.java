package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class Command {
	
	protected static TitanChat plugin;
	
	public Command(TitanChat plugin) {
		Command.plugin = plugin;
	}
	
	public static void invalidArgLength(Player player, String name) {
		plugin.sendWarning(player, "Invalid Argument Length");
		
		Method method = plugin.getCommandManager().getCommandExecutor(name).getMethod();
		
		if (method.getAnnotation(CommandInfo.class) != null)
			plugin.sendInfo(player, "Usage: /titanchat " + method.getAnnotation(CommandInfo.class).usage());
	}
	
	public static void register(Command command) {
		plugin.getCommandManager().register(command);
	}
}