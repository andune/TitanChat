package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class DemoteCommand extends Command {
	
	public DemoteCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.DEMOTE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canRank(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (cm.getChannel(args[1]).getAdminList().contains(targetPlayer.getName())) {
							Channel channel = cm.getChannel(args[1]);
							channel.getAdminList().remove(targetPlayer.getName());
							channel.save();
							
							plugin.sendInfo(targetPlayer, "You have been demoted in " + channel.getName());
							plugin.sendInfo(player, "You have demoted " + targetPlayer.getDisplayName());
							
						} else {
							plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not an Admin");
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
					
					if (cm.getChannel(player).getAdminList().contains(targetPlayer.getName())) {
						Channel channel = cm.getChannel(player);
						channel.getAdminList().remove(targetPlayer.getName());
						channel.save();
						
						plugin.sendInfo(targetPlayer, "You have been demoted in " + channel.getName());
						plugin.sendInfo(player, "You have demoted " + targetPlayer.getDisplayName());
						
					} else {
						plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not an Admin");
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