package com.titankingdoms.nodinchan.titanchat.channel.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.util.Handler.HandlerInfo;

public abstract class CommandHandler {
	
	protected final TitanChat plugin;
	
	protected final Channel channel;
	
	private final String command;
	
	private final HandlerInfo info;
	
	public CommandHandler(Channel channel, String command, HandlerInfo info) {
		this.plugin = TitanChat.getInstance();
		this.channel = channel;
		this.command = command;
		this.info = info;
	}
	
	public final String getCommand() {
		return command;
	}
	
	public final HandlerInfo getInfo() {
		return info;
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
	
	public abstract void onCommand(CommandSender sender, String[] args);
	
	public final void unspecifiedChannel(CommandSender sender) {
		if (sender instanceof Player)
			plugin.send(MessageLevel.WARNING, sender, "Please specify a channel or join a channel");
		else
			plugin.send(MessageLevel.WARNING, sender, "Please specify a channel");
		
		usage(sender);
	}
	
	public final void usage(CommandSender sender) {
		if (!info.getUsage().isEmpty())
			plugin.send(MessageLevel.WARNING, sender, "Usage: /titanchat <@><channel> " + info.getUsage());
	}
}