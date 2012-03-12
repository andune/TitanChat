package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class InfoCommand extends Command {
	
	public InfoCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.INFO, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		try {
			Channel channel = cm.getChannel(args[0]);
			
			if (channel != null) {
				if (channel.canAccess(player)) {
					player.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");
					player.sendMessage(ChatColor.AQUA + "Participants: " + plugin.createList(channel.getParticipants()));
					player.sendMessage(ChatColor.AQUA + "Followers: " + plugin.createList(channel.getFollowerList()));
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			player.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");
			player.sendMessage(ChatColor.AQUA + "Participants: " + plugin.createList(channel.getParticipants()));
			player.sendMessage(ChatColor.AQUA + "Followers: " + plugin.createList(channel.getFollowerList()));
		}
	}
}
