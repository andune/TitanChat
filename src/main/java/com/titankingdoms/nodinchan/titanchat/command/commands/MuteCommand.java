package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class MuteCommand extends Command {
	
	public MuteCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.MUTE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canMute(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						Channel channel = cm.getChannel(args[1]);
						channel.getMuteList().add(targetPlayer.getName());
						plugin.sendWarning(targetPlayer, "You have been muted on " + channel.getName());
						
						for (String participant : cm.getChannel(args[1]).getParticipants()) {
							if (plugin.getPlayer(participant) != null)
								plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been muted");
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
			if (cm.getChannel(player).canMute(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					Player targetPlayer = plugin.getPlayer(args[0]);
					Channel channel = cm.getChannel(player);
					channel.getMuteList().add(targetPlayer.getName());
					plugin.sendWarning(targetPlayer, "You have been muted on " + channel.getName());
					
					for (String participant : cm.getChannel(player).getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been muted");
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