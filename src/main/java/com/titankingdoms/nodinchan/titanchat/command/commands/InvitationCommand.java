package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;

public class InvitationCommand extends Command {
	
	private ChannelManager cm;
	
	public InvitationCommand(TitanChat plugin) {
		super(plugin);
		this.cm = plugin.getChannelManager();
	}
	
	@CommandID(name = "Accept", triggers = "accept")
	@CommandInfo(description = "Accepts the channel join invitation and joins the channel", usage = "accept [channel]")
	public void accept(Player player, String[] args) {
		try {
			if (cm.exists(args[0])) {
				Channel channel = cm.getChannel(args[0]);
				
				if (channel.getInviteList().contains(player.getName())) {
					channel.getInviteList().remove(player.getName());
					cm.onInviteRespond(channel, player, true);
					
					plugin.channelSwitch(player, cm.getChannel(player), channel);
					plugin.sendInfo(player, "You have accepted the invitation");
					
				} else { plugin.sendWarning(player, "You did not receive any invitations from this channel"); }
						
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "Accept"); }
	}
	
	@CommandID(name = "Decline", triggers = "decline")
	@CommandInfo(description = "Declines the channel join invitation", usage = "decline [channel]")
	public void decline(Player player, String[] args) {
		try {
			if (cm.exists(args[0])) {
				Channel channel = cm.getChannel(args[0]);
				
				if (channel.getInviteList().contains(player.getName())) {
					channel.getInviteList().remove(player.getName());
					cm.onInviteRespond(channel, player, false);
					
					plugin.sendInfo(player, "You have declined the invitation");
					
				} else { plugin.sendWarning(player, "You did not receive any invitations from this channel"); }
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "Decline"); }
	}
	
	@CommandID(name = "Invite", triggers = "invite")
	@CommandInfo(description = "Invites the player to join the channel", usage = "invite [player] <channel>")
	public void invite(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Invite"); }
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (channel.getAdminList().contains(player.getName())) {
					if (plugin.getPlayer(args[0]) != null) {
						channel.getInviteList().add(plugin.getPlayer(args[0]).getName());
						cm.onInvite(channel, player, plugin.getPlayer(args[0]));
						
						plugin.sendInfo(player, "You have invited " + plugin.getPlayer(args[0]).getName());
						plugin.sendInfo(plugin.getPlayer(args[0]), "You have been invited to chat on " + channel.getName());
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission to invite players on this channel"); }
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel.getAdminList().contains(player.getName())) {
				if (plugin.getPlayer(args[0]) != null) {
					channel.getInviteList().add(plugin.getPlayer(args[0]).getName());
					plugin.sendInfo(player, "You have invited " + plugin.getPlayer(args[0]).getName());
					plugin.sendInfo(plugin.getPlayer(args[0]), "You have been invited to chat on " + channel.getName());
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else { plugin.sendWarning(player, "You do not have permission to invite players on this channel"); }
		}
	}
}