package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class UnfollowCommand extends Command {
	
	public UnfollowCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.UNFOLLOW, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		try {
			if (cm.exists(args[0])) {
				if (cm.getChannel(args[0]).getFollowerList().contains(player.getName())) {
					Channel channel = cm.getChannel(args[0]);
					channel.getFollowerList().remove(player.getName());
					cm.getChannel(args[0]).save();
					
					plugin.sendInfo(player, "You have unfollowed " + channel.getName());
					
				} else {
					plugin.sendWarning(player, "You are not following " + cm.getExact(args[0]));
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player); }
	}
}