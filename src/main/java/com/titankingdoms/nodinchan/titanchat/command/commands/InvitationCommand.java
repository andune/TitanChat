package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.channel.util.Invitation.Response;
import com.titankingdoms.nodinchan.titanchat.command.CommandBase;
import com.titankingdoms.nodinchan.titanchat.command.info.*;

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
public class InvitationCommand extends CommandBase {

	private ChannelManager cm;
	
	public InvitationCommand() {
		this.cm = plugin.getManager().getChannelManager();
	}
	
	/**
	 * Accept Command - Accepts the channel join invitation and joins the channel
	 */
	@Command
	@Description("Accepts the channel join invitation and joins the channel")
	@Usage("accept [channel]")
	public void accept(Player player, String[] args) {
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[0]))
				channel = cm.getChannelByAlias(args[0]);
			else
				plugin.send(MessageLevel.WARNING, player, "No such channel");
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "accept"); }
		
		if (channel == null)
			return;
		
		cm.getParticipant(player).getInvitation().response(channel, Response.ACCEPT);
		channel.join(player);
	}
	
	/**
	 * Decline Command - Declines the channel join invitation
	 */
	@Command
	@Description("Declines the channel join invitation")
	@Usage("decline [channel]")
	public void decline(Player player, String[] args) {
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[0]))
				channel = cm.getChannelByAlias(args[0]);
			else
				plugin.send(MessageLevel.WARNING, player, "No such channel");
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "decline"); }
		
		if (channel == null)
			return;
		
		cm.getParticipant(player).getInvitation().response(channel, Response.DECLINE);
	}
	
	/**
	 * Invite Command - Invites the player to join the channel
	 */
	@Command
	@Description("Invites the player to join the channel")
	@Usage("invite [player] <channel>")
	public void invite(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "invite"); }
		
		Channel channel = null;
		Player targetPlayer = plugin.getPlayer(args[0]);
		
		if (targetPlayer == null) {
			plugin.send(MessageLevel.WARNING, player, "Player not online");
			return;
		}
		
		try {
			if (cm.existsByAlias(args[0]))
				channel = cm.getChannelByAlias(args[0]);
			else
				plugin.send(MessageLevel.WARNING, player, "No such channel");
			
		} catch (IndexOutOfBoundsException e) {
			channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, player, "Specify a channel or join a channel to use this command");
				usage(player, "invite");
				return;
			}
		}
		
		if (channel == null)
			return;
		
		cm.getParticipant(targetPlayer).getInvitation().invite(channel, player);
	}
}