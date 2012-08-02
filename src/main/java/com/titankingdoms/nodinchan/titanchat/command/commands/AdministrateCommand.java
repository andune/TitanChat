package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.command.CommandSender;

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
	 * Ban Command - Bans the player from the channel
	 */
	@Command(channel = true, server = true)
	@Aliases("b")
	@Description("Bans the player from the channel")
	@Usage("ban [player]")
	public void ban(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "ban", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "ban"); return; }
	}
	
	/**
	 * Force Command - Forces the player to join the channel
	 */
	@Command(channel = true, server = true)
	@Description("Forces the player to join the channel")
	@Permission("TitanChat.force")
	@Usage("force [player]")
	public void force(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "force", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "force"); }
	}
	
	/**
	 * Kick Command - Kicks the player from the channel
	 */
	@Command(channel = true, server = true)
	@Aliases("k")
	@Description("Kicks the player from the channel")
	@Usage("kick [player]")
	public void kick(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "kick", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "kick"); }
	}
	
	/**
	 * Mute Command - Mutes the player on the channel
	 */
	@Command(channel = true, server = true)
	@Description("Mutes the player on the channel")
	@Usage("mute [player]")
	public void mute(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "mute", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "mute"); }
	}
	
	/**
	 * Unban Command - Unbans the player from the channel
	 */
	@Command(channel = true, server = true)
	@Aliases("ub")
	@Description("Unbans the player from the channel")
	@Usage("unban [player]")
	public void unban(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "unban", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "unban"); }
	}
	
	/**
	 * Unmute Command - Unmutes the player on the channel
	 */
	@Command(channel = true, server = true)
	@Description("Unmutes the player on the channel")
	@Usage("unmute [player]")
	public void unmute(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "unmute", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "unmute"); }
	}
}