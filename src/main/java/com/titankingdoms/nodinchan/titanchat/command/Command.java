package com.titankingdoms.nodinchan.titanchat.command;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.addon.Addon;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;

public class Command {

	protected final TitanChat plugin;
	
	public Command(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public void init() {}
	
	public final void invalidArgLength(Player player, String name) {
		plugin.sendWarning(player, "Invalid Argument Length");
		
		Method method = plugin.getCommandManager().getCommandExecutor(name).getMethod();
		
		if (method.getAnnotation(CommandInfo.class) != null)
			plugin.sendInfo(player, "Usage: /titanchat " + method.getAnnotation(CommandInfo.class).usage());
	}
	
	public final void register(Addon addon) {
		plugin.getAddonManager().register(addon);
	}
	
	public final void register(CustomChannel channel) {
		plugin.getChannelManager().register(channel);
	}
	
	public final void register(Command command) {
		plugin.getCommandManager().register(command);
	}
}