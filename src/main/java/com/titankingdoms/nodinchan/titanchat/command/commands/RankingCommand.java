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
 * RankingCommand - Command for promotion, demotion and whitelisting on Channels
 * 
 * @author NodinChan
 *
 */
public class RankingCommand extends CommandBase {

	private ChannelManager cm;
	
	public RankingCommand() {
		this.cm = plugin.getManager().getChannelManager();
	}
	
	/**
	 * Demote Command - Demotes the player of the channel
	 */
	@Command(server = true)
	@Description("Demotes the player of the channel")
	@Usage("demote [player]")
	public void demote(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "demote", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "demote"); return; }
	}
	
	/**
	 * Promote Command - Promotes the player of the channel
	 */
	@Command(server = true)
	@Description("Promotes the player of the channel")
	@Usage("promote [player]")
	public void promote(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "promote", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "promote"); return; }
	}
	
	/**
	 * Whitelist Command - Whitelists the player for the channel
	 */
	@Command(channel = true, server = true)
	@Aliases("add")
	@Description("Whitelists the player for the channel")
	@Usage("whitelist [player]")
	public void whitelist(CommandSender sender, Channel channel, String[] args) {
		if (channel.handleCommand(sender, "whitelist", args))
			return;
		
		if (args.length < 1) { invalidArgLength(sender, "whitelist"); return; }
	}
}