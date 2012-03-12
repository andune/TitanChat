package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class AddCommand extends Command {
	
	public AddCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.ADD, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); return; }
		
		try {
			if (cm.exists(args[1])) {
				if (cm.getChannel(args[1]).canRank(player)) {
					if (plugin.getPlayer(args[0]) != null) {
						plugin.whitelistMember(plugin.getPlayer(args[0]), cm.getChannel(args[1]));
						plugin.sendInfo(player, plugin.getPlayer(args[0]).getDisplayName() + " has been added to the Member List");
						
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
			if (cm.getChannel(player).canRank(player)) {
				if (plugin.getPlayer(args[0]) != null) {
					plugin.whitelistMember(plugin.getPlayer(args[0]), cm.getChannel(player));
					plugin.sendInfo(player, plugin.getPlayer(args[0]).getDisplayName() + " has been added to the Member List");
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission");
			}
		}
	}
}
