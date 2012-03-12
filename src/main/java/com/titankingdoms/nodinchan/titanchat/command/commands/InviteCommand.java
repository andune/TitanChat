package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class InviteCommand extends Command {
	
	public InviteCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.INVITE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).getAdminList().contains(player.getName())) {
					if (plugin.getPlayer(args[0]) != null) {
						Channel channel = cm.getChannel(args[1]);
						channel.getInviteList().add(plugin.getPlayer(args[0]).getName());
						plugin.sendInfo(player, "You have invited " + plugin.getPlayer(args[0]).getName());
						plugin.sendInfo(plugin.getPlayer(args[0]), "You have been invited to chat on " + channel.getName());
						
					} else {
						plugin.sendWarning(player, "Player not online");
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to invite players on this channel");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			
		} catch (IndexOutOfBoundsException e) {
			if (cm.getChannel(player).getAdminList().contains(player.getName())) {
				if (plugin.getPlayer(args[0]) != null) {
					Channel channel = cm.getChannel(player);
					channel.getInviteList().add(plugin.getPlayer(args[0]).getName());
					plugin.sendInfo(player, "You have invited " + plugin.getPlayer(args[0]).getName());
					plugin.sendInfo(plugin.getPlayer(args[0]), "You have been invited to chat on " + channel.getName());
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to invite players on this channel");
			}
		}
	}
}
