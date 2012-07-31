package com.titankingdoms.nodinchan.titanchat.channel.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;

public abstract class CommandHandler {
	
	protected final TitanChat plugin;
	
	protected final Channel channel;
	
	private final String command;
	
	public CommandHandler(Channel channel, String command) {
		this.plugin = TitanChat.getInstance();
		this.channel = channel;
		this.command = command;
	}
	
	public final String getCommand() {
		return command;
	}
	
	public final boolean hasPermission(CommandSender sender, String permission) {
		return hasPermission(sender, permission, false);
	}
	
	public final boolean hasPermission(CommandSender sender, String permission, boolean avoidWildcard) {
		if (!(sender instanceof Player))
			return true;
		
		return plugin.getPermsBridge().has((Player) sender, permission, avoidWildcard);
	}
	
	public abstract void onCommand(CommandSender sender, String[] args);
}