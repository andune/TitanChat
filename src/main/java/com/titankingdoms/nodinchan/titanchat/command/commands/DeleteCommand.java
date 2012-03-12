package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;
import com.titankingdoms.nodinchan.titanchat.enums.Type;

public class DeleteCommand extends Command {
	
	public DeleteCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.DELETE, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (!plugin.enableChannels()) { plugin.sendWarning(player, "Channels are disabled"); return; }
		
		try {
			if (plugin.has(player, "TitanChat.delete")) {
				if (cm.exists(args[0])) {
					if (cm.getChannel(args[0]).getType().equals(Type.DEFAULT) || cm.getChannel(args[0]).getType().equals(Type.STAFF)) {
						plugin.getChannelManager().deleteChannel(player, args[0]);
						
					} else {
						plugin.sendWarning(player, "You cannot delete this channel");
					}
					
				} else {
					plugin.sendWarning(player, "Channel does not exists");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to delete channels");
			}
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player); }
	}
}