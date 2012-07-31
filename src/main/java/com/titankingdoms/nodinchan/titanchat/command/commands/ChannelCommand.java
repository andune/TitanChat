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
	public void create(CommandSender sender, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "create"); return; }
		
		String name = args[0];
		String type = "Standard";
		
		try { if (!args[1].isEmpty()) type = args[1]; } catch (IndexOutOfBoundsException e) {}
		
		if (name.isEmpty()) {
			plugin.send(MessageLevel.WARNING, sender, "Invalid channel name - Empty");
			return;
		}
		
		if (plugin.getConfig().getInt("channels.channel-limit", -1) < 0) {
			if (cm.nameCheck(name)) {
				if (!cm.exists(name))
					cm.createChannel(sender, name, type);
				else
					plugin.send(MessageLevel.WARNING, sender, "Channel already exists");
				
			} else { plugin.send(MessageLevel.WARNING, sender, "Channel names cannot contain \\, /, :. *, ?, \", <, > or |"); }
			
		} else if (cm.getChannels().size() < plugin.getConfig().getInt("channel.channel-limit", -1)) {
			if (cm.nameCheck(name)) {
				if (!cm.exists(name))
					cm.createChannel(sender, name, type);
				else
					plugin.send(MessageLevel.WARNING, sender, "Channel already exists");
				
			} else { plugin.send(MessageLevel.WARNING, sender, "Channel names cannot contain \\, /, :. *, ?, \", <, > or |"); }
			
		} else { plugin.send(MessageLevel.WARNING, sender, "Cannot create channel - Limit passed"); }
	}
	
	/**
	 * Delete Command - Deletes the channel
	 */
	@Command(server = true)
	@Aliases("d")
	@Description("Deletes the channel")
	@Permission("TitanChat.delete")
	@Usage("delete [channel]")
	public void delete(CommandSender sender, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "delete"); return; }
		
		if (cm.exists(args[0])) {
			Channel channel = cm.getChannelByAlias(args[0]);
			
			if (channel.getOption().equals(Option.NONE))
				cm.deleteChannel(sender, channel.getName());
			else
				plugin.send(MessageLevel.WARNING, sender, "You cannot delete this channel");
			
		} else { plugin.send(MessageLevel.WARNING, sender, "No such channel"); }
	}
	
	/**
	 * Follow Command - Follows the channel
	 */
	@Command
	@Description("Follows the channel")
	@Usage("follow [channel]")
	public void follow(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "follow"); return; }
		
		Channel channel = null;
		
		if (cm.existsByAlias(args[0]))
			channel = cm.getChannelByAlias(args[0]);
		else
			plugin.send(MessageLevel.WARNING, player, "No such channel");
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(player, "follow", args))
			return;
	}
	
	/**
	 * Join Command - Joins the channel
	 */
	@Command
	@Aliases("j")
	@Description("Joins the channel")
	@Usage("join [channel]")
	public void join(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "join"); return; }
		
		Channel channel = null;
		
		if (cm.existsByAlias(args[0]))
			channel = cm.getChannelByAlias(args[0]);
		else
			plugin.send(MessageLevel.WARNING, player, "No such channel");
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(player, "join", args))
			return;
	}
	
	/**
	 * Leave Command - Leaves the channel you are in
	 */
	@Command
	@Aliases("part")
	@Description("Leaves the channel")
	@Usage("leave <channel>")
	public void leave(Player player, String[] args) {
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[0]))
				channel = cm.getChannelByAlias(args[0]);
			else
				plugin.send(MessageLevel.WARNING, player, "No such channel");
			
		} catch (IndexOutOfBoundsException e) {
			channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, player, "Specify a channel or join a channel to use this command");
				usage(player, "leave");
			}
		}
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(player, "leave", args))
			return;
	}
	
	/**
	 * Unfollow Command - Unfollows the channel
	 */
	@Command
	@Description("Unfollows the channel")
	@Usage("unfollow [channel]")
	public void unfollow(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "unfollow"); return; }
		
		Channel channel = null;
		
		if (cm.existsByAlias(args[0]))
			channel = cm.getChannelByAlias(args[0]);
		else
			plugin.send(MessageLevel.WARNING, player, "No such channel");
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(player, "unfollow", args))
			return;
	}
}