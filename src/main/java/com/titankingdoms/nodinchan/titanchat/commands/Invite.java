package com.titankingdoms.nodinchan.titanchat.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;

public class Invite {
	
	private TitanChat plugin;
	
	public Invite(TitanChat plugin) {
		this.plugin = plugin;
	}
	
	public void accept(Player player, String channelName) {
		if (plugin.channelExist(channelName)) {
			if (plugin.getChannel(channelName).getInviteList().contains(player.getName())) {
				plugin.inviteResponse(player, channelName, true);
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
				plugin.inviteResponse(player, channelName, false);
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
					plugin.invite(plugin.getServer().getPlayer(target), channelName);
					plugin.sendInfo(player, "You have invited " + plugin.getPlayer(target).getName());
					
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