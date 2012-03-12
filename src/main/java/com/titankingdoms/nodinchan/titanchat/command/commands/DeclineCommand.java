package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class DeclineCommand extends Command {
	
	public DeclineCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.DECLINE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		try {
			if (cm.exists(args[0])) {
				if (cm.getChannel(args[0]).getInviteList().contains(player.getName())) {
					Channel channel = cm.getChannel(args[0]);
					channel.getInviteList().remove(player.getName());
					
					plugin.sendInfo(player, "You have declined the invitation");
					
				} else {
					plugin.sendWarning(player, "You did not receive any invitations from this channel");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			
		} catch (IndexOutOfBoundsException e) { command.invalidArgLength(player); }
	}
}