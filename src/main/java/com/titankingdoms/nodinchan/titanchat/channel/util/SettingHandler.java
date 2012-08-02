package com.titankingdoms.nodinchan.titanchat.channel.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;

public abstract class SettingHandler {
	
	protected final TitanChat plugin;
	
	protected final Channel channel;
	
	private final String setting;
	
	public SettingHandler(Channel channel, String setting) {
		this.plugin = TitanChat.getInstance();
		this.channel = channel;
		this.setting = setting;
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
	
	public abstract void set(CommandSender sender, String[] args);
}