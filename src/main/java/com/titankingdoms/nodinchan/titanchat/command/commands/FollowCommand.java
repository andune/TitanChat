package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class FollowCommand extends Command {

	public FollowCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.FOLLOW, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		try {
			if (cm.exists(args[0])) {
				if (cm.getChannel(args[0]).canAccess(player)) {
					if (cm.getChannel(args[0]).getFollowerList().contains(player.getName())) {
						plugin.sendWarning(player, "You are already following " + cm.getExact(args[0]));
						
					} else {
						Channel channel = cm.getChannel(args[0]);
						channel.getFollowerList().add(player.getName());
						cm.getChannel(args[0]).save();
						
						plugin.sendInfo(player, "You have followed " + channel.getName());
					}
					
				} else {
					plugin.sendWarning(player, "You do not have permission");
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player); }
	}
}