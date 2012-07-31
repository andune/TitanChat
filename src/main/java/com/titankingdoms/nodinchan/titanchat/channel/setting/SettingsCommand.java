package com.titankingdoms.nodinchan.titanchat.channel.setting;

import java.util.Arrays;

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
 * SettingsCommand - Commands for config modification
 * 
 * @author NodinChan
 *
 */
public class SettingsCommand extends CommandBase {

	private static ChannelManager cm;
	
	public SettingsCommand() {
		SettingsCommand.cm = plugin.getManager().getChannelManager();
	}
	
	/**
	 * Set Command - Sets the channel settings
	 */
	@Command
	@Description("Channel settings (\"/titanchat set help\" for more info)")
	@Usage("set [setting] <@><channel> <arguments>")
	public void set(CommandSender sender, String[] args) {
		if (args.length < 1) {
			plugin.getServer().dispatchCommand(sender, "titanchat set help");
			return;
		}
		
		Channel channel = null;
		
		try {
			if (args[1].startsWith("@")) {
				if (cm.existsByAlias(args[1].substring(1)))
					channel = cm.getChannelByAlias(args[1].substring(1));
				else
					plugin.send(MessageLevel.WARNING, sender, "No such channel");
				
			} else { throw new IndexOutOfBoundsException(); }
			
		} catch (IndexOutOfBoundsException e) {
			if (!(sender instanceof Player)) {
				plugin.send(MessageLevel.WARNING, sender, "Please specify a channel");
				usage(sender, "set");
				return;
			}
			
			channel = cm.getChannel((Player) sender);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, sender, "Specify a channel or join a channel to use this command");
				usage(sender, "set");
			}
		}
		
		if (channel == null)
			return;
		
		String[] arguments = new String[0];
		
		try {
			if (args[1].startsWith("@"))
				arguments = Arrays.copyOfRange(args, 2, args.length);
			else
				throw new IndexOutOfBoundsException();
			
		} catch (IndexOutOfBoundsException e) { arguments = Arrays.copyOfRange(args, 1, args.length); }
		
		if (!channel.changeSetting(sender, args[0], arguments))
			plugin.send(MessageLevel.WARNING, sender, "Channel " + channel.getName() + " does not support such setting");
	}
}