package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class BanCommand extends Command {
	
	private TitanChat plugin;
	
	private ChannelManager cm;
	
	public BanCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.BAN, plugin, cm);
		this.plugin = plugin;
		this.cm = cm;
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); return; }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canBan(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						Player targetPlayer = plugin.getPlayer(args[0]);
						Channel channel = cm.getChannel(args[1]);
						
						channel.getAdminList().remove(targetPlayer.getName());
						channel.getWhiteList().remove(targetPlayer.getName());
						channel.getBlackList().add(targetPlayer.getName());
						channel.save();
						
						plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(player));
						plugin.sendWarning(targetPlayer, "You have been banned from " + channel.getName());
						
						for (String participant : cm.getChannel(player).getParticipants()) {
							if (plugin.getPlayer(participant) != null)
								plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been banned from the channel");
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
					Channel channel = cm.getChannel(player);
					
					channel.getAdminList().remove(targetPlayer.getName());
					channel.getWhiteList().remove(targetPlayer.getName());
					channel.getBlackList().add(targetPlayer.getName());
					channel.save();
					
					plugin.channelSwitch(targetPlayer, channel, cm.getSpawnChannel(player));
					plugin.sendWarning(targetPlayer, "You have been banned from " + channel.getName());
					
					for (String participant : cm.getChannel(player).getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							plugin.getPlayer(participant).sendMessage(targetPlayer.getDisplayName() + " has been banned from the channel");
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