package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.commands.Command;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class ForceCommand extends Command {
	
	public ForceCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.FORCE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); }
		
		try {
			if (cm.exists(args[1])) {
				if (plugin.has(player, "TitanChat.force")) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						plugin.channelSwitch(targetPlayer, cm.getChannel(targetPlayer), cm.getChannel(args[1]));
						plugin.sendInfo(player, "You have forced " + targetPlayer.getDisplayName() + " to join the channel");
						plugin.sendInfo(targetPlayer, "You have been forced to join " + cm.getExact(args[1]));
						
					} else {
						plugin.sendWarning(player, "Player not online");
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			
		} catch (IndexOutOfBoundsException e) {
			if (plugin.has(player, "TitanChat.force")) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					plugin.channelSwitch(targetPlayer, cm.getChannel(targetPlayer), cm.getChannel(player));
					plugin.sendInfo(player, "You have forced " + targetPlayer.getDisplayName() + " to join the channel");
					plugin.sendInfo(targetPlayer, "You have been forced to join " + cm.getChannel(player).getName());
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
}
