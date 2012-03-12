package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class PromoteCommand extends Command {
	
	public PromoteCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.PROMOTE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); }
		
		try {
			if (cm.exists(args[0])) {
				if (cm.getChannel(args[0]).canRank(player)) {
					if (plugin.getPlayer(args[1]) != null) {
						Player targetPlayer = plugin.getPlayer(args[1]);
						
						if (cm.getChannel(args[0]).getAdminList().contains(player.getName())) {
							plugin.sendWarning(player, targetPlayer.getDisplayName() + " is already an Admin");
							
						} else {
							plugin.assignAdmin(targetPlayer, cm.getChannel(args[0]));
							plugin.sendInfo(player, "You have been promoted in " + cm.getExact(args[0]));
							plugin.sendInfo(player, "You have promoted " + targetPlayer.getDisplayName());
						}
						
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
			if (cm.getChannel(player).canRank(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (cm.getChannel(player).getAdminList().contains(player.getName())) {
						plugin.sendWarning(player, targetPlayer.getDisplayName() + " is already an Admin");
						
					} else {
						plugin.assignAdmin(targetPlayer, cm.getChannel(player));
						plugin.sendInfo(player, "You have been promoted in " + cm.getChannel(player).getName());
						plugin.sendInfo(player, "You have promoted " + targetPlayer.getDisplayName());
					}
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
}