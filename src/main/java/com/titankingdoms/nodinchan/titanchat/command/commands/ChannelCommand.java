package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.channel.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.channel.StandardChannel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Type;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;

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
 * ChannelCommand - Commands for Channel modification
 * 
 * @author NodinChan
 *
 */
public class ChannelCommand extends Command {

	private ChannelManager cm;
	
	public ChannelCommand() {
		this.cm = plugin.getChannelManager();
	}
	
	@CommandID(name = "Create", triggers = { "create", "c" })
	@CommandInfo(description = "Creates a new channel", usage = "create [channel]")
	public void create(Player player, String[] args) {
		try {
			if (plugin.getConfig().getInt("channels.channel-limit") < 0) {
				if (plugin.getPermsBridge().has(player, "TitanChat.create")) {
					if (!cm.exists(args[0])) {
						plugin.getChannelManager().createChannel(player, args[0]);
						
					} else { plugin.sendWarning(player, "Channel already exists"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else if (plugin.getChannelManager().getChannelAmount() < plugin.getConfig().getInt("channel-limit")) {
				if (plugin.getPermsBridge().has(player, "TitanChat.create")) {
					if (!cm.exists(args[0])) {
						plugin.getChannelManager().createChannel(player, args[0]);
						
					} else { plugin.sendWarning(player, "Channel already exists"); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else { plugin.sendWarning(player, "Cannot create channel - Limit Passed"); }
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "Create"); }
	}
	
	@CommandID(name = "Delete", triggers = { "delete", "d" })
	@CommandInfo(description = "Deletes the channel", usage = "delete [channel]")
	public void delete(Player player, String[] args) {
		try {
			if (plugin.getPermsBridge().has(player, "TitanChat.delete")) {
				if (cm.exists(args[0])) {
					if (!cm.getChannel(args[0]).getType().equals(Type.DEFAULT) || !cm.getChannel(args[0]).getType().equals(Type.STAFF))
						plugin.getChannelManager().deleteChannel(player, args[0]);
					else { plugin.sendWarning(player, "You cannot delete this channel"); }
					
				} else { plugin.sendWarning(player, "Channel does not exists"); }
				
			} else { plugin.sendWarning(player, "You do not have permission to delete channels"); }
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "Delete"); }
	}
	
	@CommandID(name = "Follow", triggers = "follow")
	@CommandInfo(description = "Follows the channel", usage = "follow [channel]")
	public void follow(Player player, String[] args) {
		try {
			if (cm.exists(args[0])) {
				Channel channel = cm.getChannel(args[0]);
				
				if (channel.canAccess(player)) {
					if (!cm.getFollowers(channel).contains(player.getName())) {
						channel.getFollowerList().add(player.getName());
						channel.save();
						
						plugin.sendInfo(player, "You have followed " + channel.getName());
						
					} else { plugin.sendWarning(player, "You are already following " + channel.getName()); }
					
				} else { plugin.sendWarning(player, "You do not have permission"); }
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "Follow"); }
	}
	
	@CommandID(name = "Join", triggers = { "join", "j" })
	@CommandInfo(description = "Joins the channel", usage = "join [channel] <password>")
	public void join(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Join"); return; }
		
		if (cm.exists(args[0])) {
			Channel ch = cm.getChannel(args[0]);
			String password = "";
			
			if (cm.getChannel(player).equals(ch)) { plugin.sendWarning(player, "You are already on the channel"); return; }
			
			try { password = args[1]; } catch (IndexOutOfBoundsException e) {}
			
			if (ch instanceof CustomChannel) {
				if (ch.canAccess(player)) {
					cm.chSwitch(player, ch);
					plugin.sendInfo(player, "You have switched channels");
					
				} else { plugin.sendWarning(player, "You do not have permission to join " + ch.getName()); }
				
				return;
			}
			
			StandardChannel channel = (StandardChannel) ch;
			
			switch (channel.getType()) {
			
			case DEFAULT:
				cm.chSwitch(player, channel);
				plugin.sendInfo(player, "You have switched channels");
				break;
				
			case PASSWORD:
				if (!password.equals("")) {
					if (channel.correctPassword(password)) {
						if (channel.canAccess(player)) {
							cm.chSwitch(player, channel);
							plugin.sendInfo(player, "You have switched channels");
							
						} else { plugin.sendWarning(player, "You are banned on this channel"); }
						
					} else { plugin.sendWarning(player, "Incorrect password"); }
					
				} else { plugin.sendWarning(player, "You need to enter a password"); }
				break;
				
			case PRIVATE:
				if (channel.canAccess(player)) {
					cm.chSwitch(player, channel);
					plugin.sendInfo(player, "You have switched channels");
					
				} else { plugin.sendWarning(player, "You are not on the whitelist"); }
				break;
				
			case PUBLIC:
				if (channel.canAccess(player)) {
					cm.chSwitch(player, channel);
					plugin.sendInfo(player, "You have switched channels");
					
				} else { plugin.sendWarning(player, "You are banned on this channel"); }
				break;
				
			case STAFF:
				if (plugin.isStaff(player)) {
					cm.chSwitch(player, channel);
					plugin.sendInfo(player, "You have switched channels");
					
				} else { plugin.sendWarning(player, "You do not have permission to join " + channel.getName()); }
				break;
			}
		}
	}
	
	@CommandID(name = "Unfollow", triggers = "unfollow")
	@CommandInfo(description = "Unfollows the channel", usage = "unfollow [channel]")
	public void unfollow(Player player, String[] args) {
		try {
			if (cm.exists(args[0])) {
				Channel channel = cm.getChannel(args[0]);
				
				if (cm.getFollowers(channel).contains(player.getName())) {
					if (channel.getFollowerList().contains(player.getName()))
						channel.getFollowerList().remove(player.getName());
					else
						plugin.getPermsBridge().removePermission(player, "TitanChat.follow." + channel.getName());
					
					channel.save();
					
					plugin.sendInfo(player, "You have unfollowed " + channel.getName());
					
				} else { plugin.sendWarning(player, "You are not following " + channel.getName()); }
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) { invalidArgLength(player, "Unfollow"); }
	}
}