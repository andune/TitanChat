package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class Command {
	
	protected TitanChat plugin;
	
	protected ChannelManager cm;
	
	protected static Commands command;
	
	public Command(Commands command, TitanChat plugin, ChannelManager cm) {
		this.plugin = plugin;
		this.cm = cm;
		Command.command = command;
	}
	
	public void execute(Player player, String[] args) throws Exception {
		Class<? extends Command> commandClass = command.getCommand();
		Constructor<? extends Command> ctor = commandClass.getConstructor(plugin.getClass(), cm.getClass());
		Command cmd = ctor.newInstance(plugin, cm);
		cmd.execute(player, args);
	}
	
	public static void invalidArgLength(Player player) {
		command.invalidArgLength(player);
	}
}