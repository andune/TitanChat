package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
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
 * AdministrateCommand - Commands for Channel administration
 * 
 * @author NodinChan
 *
 */
public class AdministrateCommand extends CommandBase {

	private ChannelManager cm;
	
	public AdministrateCommand() {
		this.cm = plugin.getManager().getChannelManager();
	}
	
	/**
	 * Ban Command - Bans the sender from the channel
	 */
	@Command(server = true)
	@Aliases("b")
	@Description("Bans the sender from the channel")
	@Usage("ban [sender] <channel>")
	public void ban(CommandSender sender, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "ban"); return; }
		
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[1]))
				channel = cm.getChannelByAlias(args[1]);
			else
				plugin.send(MessageLevel.WARNING, sender, "No such channel");
			
		} catch (IndexOutOfBoundsException e) {
			if (!(sender instanceof Player)) {
				plugin.send(MessageLevel.WARNING, sender, "Please specify a channel");
				usage(sender, "ban");
				return;
			}
			
			channel = cm.getChannel((Player) sender);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, sender, "Specify a channel or join a channel to use this command");
				usage(sender, "ban");
			}
		}
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(sender, "ban", args))
			return;
	}
	
	/**
	 * Force Command - Forces the sender to join the channel
	 */
	@Command(server = true)
	@Description("Forces the sender to join the channel")
	@Permission("TitanChat.force")
	@Usage("force [sender] <channel>")
	public void force(CommandSender sender, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "force"); }
		
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[1]))
				channel = cm.getChannelByAlias(args[1]);
			else
				plugin.send(MessageLevel.WARNING, sender, "No such channel");
			
		} catch (IndexOutOfBoundsException e) {
			if (!(sender instanceof Player)) {
				plugin.send(MessageLevel.WARNING, sender, "Please specify a channel");
				usage(sender, "force");
				return;
			}
			
			channel = cm.getChannel((Player) sender);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, sender, "Specify a channel or join a channel to use this command");
				usage(sender, "force");
			}
		}
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(sender, "force", args))
			return;
	}
	
	/**
	 * Kick Command - Kicks the sender from the channel
	 */
	@Command(server = true)
	@Aliases("k")
	@Description("Kicks the sender from the channel")
	@Usage("kick [sender] <channel>")
	public void kick(CommandSender sender, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "kick"); }
		
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[1]))
				cm.getChannelByAlias(args[1]);
			else
				plugin.send(MessageLevel.WARNING, sender, "No such channel");
			
		} catch (IndexOutOfBoundsException e) {
			if (!(sender instanceof Player)) {
				plugin.send(MessageLevel.WARNING, sender, "Please specify a channel");
				usage(sender, "kick");
				return;
			}
			
			channel = cm.getChannel((Player) sender);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, sender, "Specify a channel or join a channel to use this command");
				usage(sender, "kick");
			}
		}
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(sender, "kick", args))
			return;
	}
	
	/**
	 * Mute Command - Mutes the sender on the channel
	 */
	@Command(server = true)
	@Description("Mutes the sender on the channel")
	@Usage("mute [sender] <channel>")
	public void mute(CommandSender sender, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "mute"); }
		
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[1]))
				channel = cm.getChannelByAlias(args[1]);
			else
				plugin.send(MessageLevel.WARNING, sender, "No such channel");
			
		} catch (IndexOutOfBoundsException e) {
			if (!(sender instanceof Player)) {
				plugin.send(MessageLevel.WARNING, sender, "Please specify a channel");
				usage(sender, "mute");
				return;
			}
			
			channel = cm.getChannel((Player) sender);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, sender, "Specify a channel or join a channel to use this command");
				usage(sender, "mute");
			}
		}
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(sender, "mute", args))
			return;
	}
	
	/**
	 * Unban Command - Unbans the sender from the channel
	 */
	@Command(server = true)
	@Aliases("ub")
	@Description("Unbans the sender from the channel")
	@Usage("unban [sender] <channel>")
	public void unban(CommandSender sender, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "unban"); }
		
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[1]))
				channel = cm.getChannelByAlias(args[1]);
			else
				plugin.send(MessageLevel.WARNING, sender, "No such channel");
			
		} catch (IndexOutOfBoundsException e) {
			if (!(sender instanceof Player)) {
				plugin.send(MessageLevel.WARNING, sender, "Please specify a channel");
				usage(sender, "unban");
				return;
			}
			
			channel = cm.getChannel((Player) sender);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, sender, "Specify a channel or join a channel to use this command");
				usage(sender, "unban");
			}
		}
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(sender, "unban", args))
			return;
	}
	
	/**
	 * Unmute Command - Unmutes the sender on the channel
	 */
	@Command(server = true)
	@Description("Unmutes the sender on the channel")
	@Usage("unmute [sender] <channel>")
	public void unmute(CommandSender sender, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "unmute"); }
		
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[1]))
				channel = cm.getChannelByAlias(args[1]);
			else
				plugin.send(MessageLevel.WARNING, sender, "No such channel");
			
		} catch (IndexOutOfBoundsException e) {
			if (!(sender instanceof Player)) {
				plugin.send(MessageLevel.WARNING, sender, "Please specify a channel");
				usage(sender, "unmute");
				return;
			}
			
			channel = cm.getChannel((Player) sender);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, sender, "Specify a channel or join a channel to use this command");
				usage(sender, "unmute");
			}
		}
		
		if (channel == null)
			return;
		
		if (channel.handleCommand(sender, "unmute", args))
			return;
	}
}