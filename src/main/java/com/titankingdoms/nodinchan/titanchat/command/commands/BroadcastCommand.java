package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class BroadcastCommand extends Command {
	
	public BroadcastCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.BROADCAST, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1 || !plugin.has(player, "TitanChat.broadcast")) { return; }
		
		StringBuilder broadcastStr = new StringBuilder();
		
		for (String word : args) {
			if (broadcastStr.length() > 1)
				broadcastStr.append(" ");
			
			broadcastStr.append(word);
		}
		
		plugin.getServer().broadcastMessage(plugin.getFormat().broadcastFormat(player, broadcastStr.toString()));
		plugin.getLogger().info("<" + player.getName() + "> " + broadcastStr.toString());
	}
}