package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class CreateCommand extends Command {
	
	public CreateCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.CREATE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (!plugin.enableChannels()) { plugin.sendWarning(player, "Channels are disabled"); return; }
		
		try {
			if (plugin.getConfig().getInt("channel-limit") < 0) {
				if (plugin.has(player, "TitanChat.create")) {
					if (cm.exists(args[0])) {
						plugin.sendWarning(player, "Channel already exists");
						
					} else {
						plugin.getChannelManager().createChannel(player, args[0]);
						plugin.getChannelManager().createChannel(player, args[0]);
						plugin.sendInfo(player, "You have created " + args[0]);
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to create channels");
				}
				
			} else if (plugin.getChannelManager().getChannelAmount() < plugin.getConfig().getInt("channel-limit")) {
				if (plugin.has(player, "TitanChat.create")) {
					if (cm.exists(args[0])) {
						plugin.sendWarning(player, "Channel already exists");
						
					} else {
						plugin.getChannelManager().createChannel(player, args[0]);
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission to create channels");
				}
				
			} else {
				plugin.sendWarning(player, "Cannot create channel - Limit Passed");
			}
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player); }
	}
}