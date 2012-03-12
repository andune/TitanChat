package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class UnbanCommand extends Command {
	
	public UnbanCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.UNBAN, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canBan(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						
						if (cm.getChannel(args[1]).getBlackList().contains(targetPlayer.getName())) {
							Channel channel = cm.getChannel(args[1]);
							channel.getBlackList().remove(targetPlayer.getName());
							
							plugin.whitelistMember(targetPlayer, channel);
							plugin.sendInfo(targetPlayer, "You have been unbanned from " + channel.getName());
							plugin.sendInfo(player, "You have unbanned " + targetPlayer.getDisplayName());
							
						} else {
							plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not banned");
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
			if (cm.getChannel(player).canBan(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					
					if (cm.getChannel(player).getBlackList().contains(targetPlayer.getName())) {
						Channel channel = cm.getChannel(player);
						channel.getBlackList().remove(targetPlayer.getName());
						
						plugin.whitelistMember(targetPlayer, channel);
						plugin.sendInfo(targetPlayer, "You have been unbanned from " + channel.getName());
						plugin.sendInfo(player, "You have unbanned " + targetPlayer.getDisplayName());
						
					} else {
						plugin.sendWarning(player, targetPlayer.getDisplayName() + " is not banned");
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