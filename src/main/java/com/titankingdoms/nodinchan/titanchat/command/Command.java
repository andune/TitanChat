package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.support.Addon;

public class Command {
	
	protected static TitanChat plugin;
	
	public Command(TitanChat plugin) {
		Command.plugin = plugin;
	}
	
	public Command(Addon addon) {
		Command.plugin = addon.getPlugin();
	}
	
	public Command(CustomChannel customChannel) {
		Command.plugin = customChannel.getPlugin();
	}
	
	public void init() {}
	
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