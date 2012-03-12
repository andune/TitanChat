package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class ListCommand extends Command {
	
	public ListCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.LIST, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		List<String> channels = new ArrayList<String>();
		
		for (Channel channel : cm.getChannels()) {
			if (channel.canAccess(player))
				channels.add(channel.getName());
		}
		
		player.sendMessage(ChatColor.AQUA + "Channels: " + plugin.createList(channels));
	}
}