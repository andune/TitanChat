package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class JoinCommand extends Command {
	
	public JoinCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.JOIN, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player); return; }
		
		if (cm.exists(args[0])) {
			Channel channel = cm.getChannel(args[0]);
			String password = "";
			
			try { password = args[1]; } catch (IndexOutOfBoundsException e) {}
			
			switch (channel.getType()) {
			
			case CUSTOM:
				if (channel.canAccess(player)) {
					plugin.channelSwitch(player, cm.getChannel(player), channel);
					plugin.sendInfo(player, "You have switched channels");
					
				} else {
					plugin.sendWarning(player, "You do not have permission to join " + channel.getName());
				}
				break;
			
			case DEFAULT:
				plugin.channelSwitch(player, cm.getChannel(player), channel);
				plugin.sendInfo(player, "You have switched channels");
				break;
				
			case PASSWORD:
				if (password.equals("")) {
					plugin.sendWarning(player, "You need to enter a password");
					
				} else {
					if (channel.correctPassword(password)) {
						if (channel.canAccess(player)) {
							plugin.channelSwitch(player, cm.getChannel(player), channel);
							plugin.sendInfo(player, "You have switched channels");
							
						} else {
							plugin.sendWarning(player, "You are banned on this channel");
						}
						
					} else {
						plugin.sendWarning(player, "Incorrect password");
					}
				}
				break;
				
			case PRIVATE:
				if (channel.canAccess(player)) {
					plugin.channelSwitch(player, cm.getChannel(player), channel);
					plugin.sendInfo(player, "You have switched channels");
					
				} else {
					plugin.sendWarning(player, "You are not on the whitelist");
				}
				break;
				
			case PUBLIC:
				if (channel.canAccess(player)) {
					plugin.channelSwitch(player, cm.getChannel(player), channel);
					plugin.sendInfo(player, "You have switched channels");
					
				} else {
					plugin.sendWarning(player, "You are banned on this channel");
				}
				break;
				
			case STAFF:
				if (plugin.isStaff(player)) {
					plugin.channelSwitch(player, cm.getChannel(player), channel);
					plugin.sendInfo(player, "You have switched channels");
					
				} else {
					plugin.sendWarning(player, "You do not have permission to join " + channel.getName());
				}
				break;
			}
		}
	}
}