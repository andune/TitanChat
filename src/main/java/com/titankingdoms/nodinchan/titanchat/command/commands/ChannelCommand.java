package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Option;
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
 * ChannelCommand - Commands for Channel modification
 * 
 * @author NodinChan
 *
 */
public class ChannelCommand extends CommandBase {

	private ChannelManager cm;
	
	public ChannelCommand() {
		this.cm = plugin.getManager().getChannelManager();
	}
	
	/**
	 * Create Command - Creates a new channel
	 */
	@Command(server = true)
	@Aliases("c")
	@Description("Creates a new channel")
	@Permission("TitanChat.create")
	@Usage("create [channel]")
	public void create(CommandSender sender, Channel channel, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "create"); return; }
		
		String name = args[0];
		String type = "Standard";
		
		try { if (!args[1].isEmpty()) type = args[1]; } catch (IndexOutOfBoundsException e) {}
		
		if (name.isEmpty()) {
			plugin.send(MessageLevel.WARNING, sender, "Invalid channel name - Empty");
			return;
		}
		
		if (!cm.nameCheck(name)) {
			plugin.send(MessageLevel.WARNING, sender, "Channel names cannot contain \\, /, :. *, ?, \", <, > or |");
			return;
		}
		
		if (cm.exists(name)) {
			plugin.send(MessageLevel.WARNING, sender, "Channel already exists");
			return;
		}
		
		if (plugin.getConfig().getInt("channels.channel-limit", -1) >= 0) {
			if (cm.getChannels().size() >= plugin.getConfig().getInt("channel.channel-limit", -1)) {
				plugin.send(MessageLevel.WARNING, sender, "Cannot create channel - Limit passed");
				return;
			}
		}
		
		cm.createChannel(sender, name, type);
	}
	
	/**
	 * Delete Command - Deletes the channel
	 */
	@Command(channel = true, server = true)
	@Aliases("d")
	@Description("Deletes the channel")
	@Permission("TitanChat.delete")
	@Usage("delete")
	public void delete(CommandSender sender, Channel channel, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "delete"); return; }
		
		if (channel.getOption().equals(Option.NONE))
			cm.deleteChannel(sender, channel.getName());
		else
			plugin.send(MessageLevel.WARNING, sender, "You cannot delete this channel");
	}
	
	/**
	 * Follow Command - Follows the channel
	 */
	@Command(channel = true)
	@Description("Follows the channel")
	@Usage("follow")
	public void follow(Player player, Channel channel, String[] args) {
		if (channel.handleCommand(player, "follow", args))
			return;
		
		if (channel.access(player)) {
			if (!channel.getFollowers().contains(player.getName())) {
				channel.getFollowers().add(player.getName());
				channel.save();
				
				plugin.send(MessageLevel.INFO, player, "You have followed " + channel.getName());
				
			} else { plugin.send(MessageLevel.WARNING, player, "You are already following " + channel.getName()); }
			
		} else { plugin.send(MessageLevel.WARNING, player, "You do not have access"); }
	}
	
	/**
	 * Join Command - Joins the channel
	 */
	@Command(channel = true)
	@Aliases("j")
	@Description("Joins the channel")
	@Usage("join <password>")
	public void join(Player player, Channel channel, String[] args) {
		if (channel.handleCommand(player, "join", args))
			return;
		
		if (channel.getOption().equals(Option.STAFF) && !plugin.isStaff(player)) {
			channel.deny(player, null);
			return;
		}
		
		if (!(hasPermission(player, "TitanChat.access.*") || hasPermission(player, "TitanChat.access." + channel.getName()))) {
			if (channel.getPassword() != null && !channel.getPassword().equals("")) {
				try {
					String password = args[0];
					
					if (!channel.getPassword().equals(password)) {
						channel.deny(player, "Incorrect password");
						return;
					}
					
				} catch (IndexOutOfBoundsException e) {
					channel.deny(player, "Please enter a password");
					return;
				}
			}
			
			if (channel.getBlacklist().contains(player.getName())) {
				channel.deny(player, "You are banned");
				return;
			}
			
			if (channel.getInfo().whitelistOnly() && !channel.getWhitelist().contains(player.getName())) {
				channel.deny(player, "You are not whitelisted");
				return;
			}
		}
		
		if (!channel.isParticipating(player.getName())) {
			channel.join(player);
			plugin.send(MessageLevel.INFO, player, "You have joined " + channel.getName());
			
		} else { plugin.send(MessageLevel.WARNING, player, "You are already in the channel"); }
	}
	
	/**
	 * Leave Command - Leaves the channel you are in
	 */
	@Command(channel = true)
	@Aliases("part")
	@Description("Leaves the channel")
	@Usage("leave")
	public void leave(Player player, Channel channel, String[] args) {
		if (channel.handleCommand(player, "leave", args))
			return;
		
		if (channel.isParticipating(player.getName())) {
			channel.leave(player);
			plugin.send(MessageLevel.INFO, player, "You have left " + channel.getName());
			
		} else { plugin.send(MessageLevel.WARNING, player, "You are not in the channel"); }
	}
	
	/**
	 * Unfollow Command - Unfollows the channel
	 */
	@Command(channel = true)
	@Description("Unfollows the channel")
	@Usage("unfollow")
	public void unfollow(Player player, Channel channel, String[] args) {
		if (channel.handleCommand(player, "unfollow", args))
			return;
		
		if (channel.access(player)) {
			if (channel.getFollowers().contains(player.getName())) {
				channel.getFollowers().remove(player.getName());
				channel.save();
				
				plugin.send(MessageLevel.INFO, player, "You have unfollowed " + channel.getName());
				
			} else { plugin.send(MessageLevel.WARNING, player, "You are not following " + channel.getName()); }
			
		} else { plugin.send(MessageLevel.WARNING, player, "You do not have access"); }
	}
}