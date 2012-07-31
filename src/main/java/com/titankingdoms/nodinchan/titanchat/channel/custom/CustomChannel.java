package com.titankingdoms.nodinchan.titanchat.channel.custom;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;

public class CustomChannel extends Channel {
	
	public CustomChannel(String name) {
		super(name, Option.CUSTOM);
	}
	
	@Override
	public boolean access(Player player) {
		return true;
	}
	
	@Override
	public final Channel create(CommandSender sender, String name, Option option) {
		return this;
	}

	@Override
	public final String getType() {
		return "Custom";
	}

	@Override
	public final Channel load(String name, Option option) {
		return this;
	}
}