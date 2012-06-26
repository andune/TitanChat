package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.info.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.info.CommandInfo;

/*     Copyright (C) 2012  Nodin Chan <nodinchan@live.com>
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * InvitationCommand - Commands for invitations
 * 
 * @author NodinChan
 *
 */
public class InvitationCommand extends Command {

	private ChannelManager cm;
	
	public InvitationCommand() {
		this.cm = plugin.getManager().getChannelManager();
	}
	
	/**
	 * Accept Command - Accepts the channel join invitation and joins the channel
	 */
	@CommandID(name = "Accept", aliases = "accept")
	@CommandInfo(description = "Accepts the channel join invitation and joins the channel", usage = "accept [channel]")
	public void accept(Player player, String[] args) {
		try {
			if (cm.exists(args[0])) {
				Channel channel = cm.getChannel(args[0]);
				
				if (channel.getInviteList().contains(player.getName())) {
					channel.getInviteList().remove(player.getName());
					cm.onInviteRespond(channel, player, true);
					
					cm.chSwitch(player, channel);
					plugin.sendInfo(player, "You have accepted the invitation");
					
				} else { plugin.sendWarning(player, "You did not receive any invitations from this channel"); }
						
			} else {
				if (args[0].toLowerCase().startsWith("tag:")) {
					if (cm.existsAsTag(args[0].substring(4))) {
						Channel channel = cm.getChannelByTag(args[0].substring(4));
						
						if (channel.getInviteList().contains(player.getName())) {
							channel.getInviteList().remove(player.getName());
							cm.onInviteRespond(channel, player, true);
							
							cm.chSwitch(player, channel);
							plugin.sendInfo(player, "You have accepted the invitation");
							
						} else { plugin.sendWarning(player, "You did not receive any invitations from this channel"); }
						
					} else { plugin.sendWarning(player, "No such channel"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
			}
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "Accept"); }
	}
	
	/**
	 * Decline Command - Declines the channel join invitation
	 */
	@CommandID(name = "Decline", aliases = "decline")
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
				
			} else {
				if (args[0].toLowerCase().startsWith("tag:")) {
					if (cm.existsAsTag(args[0].substring(4))) {
						Channel channel = cm.getChannelByTag(args[0].substring(4));
						
						if (channel.getInviteList().contains(player.getName())) {
							channel.getInviteList().remove(player.getName());
							cm.onInviteRespond(channel, player, false);
							
							plugin.sendInfo(player, "You have declined the invitation");
							
						} else { plugin.sendWarning(player, "You did not receive any invitations from this channel"); }
						
					} else { plugin.sendWarning(player, "No such channel"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
			}
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "Decline"); }
	}
	
	/**
	 * Invite Command - Invites the player to join the channel
	 */
	@CommandID(name = "Invite", aliases = "invite")
	@CommandInfo(description = "Invites the player to join the channel", usage = "invite [player] <channel>")
	public void invite(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Invite"); }
		
		try {
			if (cm.exists(args[1])) {
				Channel channel = cm.getChannel(args[1]);
				
				if (cm.getAdmins(channel).contains(player.getName())) {
					if (plugin.getPlayer(args[0]) != null) {
						channel.getInviteList().add(plugin.getPlayer(args[0]).getName());
						cm.onInvite(channel, player, plugin.getPlayer(args[0]));
						
						plugin.sendInfo(player, "You have invited " + plugin.getPlayer(args[0]).getName());
						plugin.sendInfo(plugin.getPlayer(args[0]), "You have been invited to chat on " + channel.getName());
						
					} else { plugin.sendWarning(player, "Player not online"); }
					
				} else { plugin.sendWarning(player, "You do not have permission to invite players on this channel"); }
				
			} else {
				if (args[1].toLowerCase().startsWith("tag:")) {
					if (cm.existsAsTag(args[1].substring(4))) {
						Channel channel = cm.getChannelByTag(args[1].substring(4));
						
						if (cm.getAdmins(channel).contains(player.getName())) {
							if (plugin.getPlayer(args[0]) != null) {
								channel.getInviteList().add(plugin.getPlayer(args[0]).getName());
								cm.onInvite(channel, player, plugin.getPlayer(args[0]));
								
								plugin.sendInfo(player, "You have invited " + plugin.getPlayer(args[0]).getName());
								plugin.sendInfo(plugin.getPlayer(args[0]), "You have been invited to chat on " + channel.getName());
								
							} else { plugin.sendWarning(player, "Player not online"); }
							
						} else { plugin.sendWarning(player, "You do not have permission to invite players on this channel"); }
						
					} else { plugin.sendWarning(player, "No such channel"); }
					
				} else { plugin.sendWarning(player, "No such channel"); }
			}
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.sendWarning(player, "Specify a channel or join a channel to use this command");
				return;
			}
			
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