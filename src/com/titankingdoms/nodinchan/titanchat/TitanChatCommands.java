package com.titankingdoms.nodinchan.titanchat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TitanChatCommands implements CommandExecutor {
	
	private TitanChat plugin;
	private ChannelManager chManager;
	
	public TitanChatCommands(TitanChat plugin) {
		this.plugin = plugin;
		this.chManager = new ChannelManager(plugin);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		
		if (label.equalsIgnoreCase("titanchat")) {
			if (args[0].equalsIgnoreCase("ban")) {
				
				
			}
			
			if (args[0].equalsIgnoreCase("create")) {
				if (player.hasPermission("TitanChat.create")) {
					plugin.sendWarning(player, "You do not have permission to create channels");
					return false;
				}
				
				String channelName = args[0];
				plugin.assignAdmin(player, channelName);
				
			}
			
			if (args[0].equalsIgnoreCase("join")) {
				if (plugin.canJoinFreely(args[1])) {
					if (plugin.channelExist(args[1])) {
						plugin.channelSwitch(player, args[1]);
						
					} else {
						plugin.sendWarning(player, "No such channel");
					}
					
				} else {
					if (plugin.isAdmin(player) || plugin.isMember(player)) {
						
						
					} else {
						plugin.sendWarning(player, "You do not have permission to join this channel");
					}
				}
			}
			
			if (args[0].equalsIgnoreCase("prefix")) {
				if (plugin.isAdmin(player)) {
					chManager.setPrefix(plugin.getChannel(player), args[1]);
					
				} else {
					plugin.sendWarning(player, "You do not have permission to change the channel tag");
				}
			}
		}
		return false;
	}
}
