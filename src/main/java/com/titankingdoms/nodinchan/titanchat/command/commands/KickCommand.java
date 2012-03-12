package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class KickCommand extends Command {
	
	public KickCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.KICK, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canKick(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						Channel channel = cm.getChannel(args[1]);
						
						plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(targetPlayer));
						plugin.sendWarning(targetPlayer, "You have been kicked from " + channel.getName());
						
						for (String participant : channel.getParticipants()) {
							if (plugin.getPlayer(participant) != null)
								plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been kicked from the channel");
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
			if (cm.getChannel(player).canKick(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					Channel channel = cm.getChannel(player);
					
					plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(targetPlayer));
					plugin.sendWarning(targetPlayer, "You have been kicked from " + channel.getName());
					
					for (String participant : channel.getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been kicked from the channel");
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