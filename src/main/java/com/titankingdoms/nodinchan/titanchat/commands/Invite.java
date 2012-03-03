package com.titankingdoms.nodinchan.titanchat.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;

public class Invite {
	
	private TitanChat plugin;
	
	public Invite(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public void accept(Player player, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getInviteList().contains(player.getName())) {
				Channel channel = plugin.getChannel(channelName);
				channel.getInviteList().remove(player.getName());
				
				plugin.channelSwitch(player, plugin.getChannel(player), channel);
				plugin.sendInfo(player, "You have accepted the invitation");
				
			} else {
				plugin.sendWarning(player, "You did not receive any invitations from this channel");
			}
					
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void decline(Player player, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getInviteList().contains(player.getName())) {
				Channel channel = plugin.getChannel(channelName);
				channel.getInviteList().remove(player.getName());
				
				plugin.sendInfo(player, "You have declined the invitation");
				
			} else {
				plugin.sendWarning(player, "You did not receive any invitations from this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
	
	public void invite(Player player, String target, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getAdminList().contains(player.getName())) {
				if (plugin.getPlayer(target) != null) {
					Channel channel = plugin.getChannel(channelName);
					channel.getInviteList().add(plugin.getPlayer(target).getName());
					plugin.sendInfo(player, "You have invited " + plugin.getPlayer(target).getName());
					plugin.sendInfo(plugin.getPlayer(target), "You have been invited to chat on " + channel.getName());
					
				} else {
					plugin.sendWarning(player, "Player not online");
				}
				
			} else {
				plugin.sendWarning(player, "You do not have permission to invite players on this channel");
			}
			
		} else {
			plugin.sendWarning(player, "No such channel");
		}
	}
}