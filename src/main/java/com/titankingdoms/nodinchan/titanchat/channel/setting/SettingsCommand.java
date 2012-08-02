package com.titankingdoms.nodinchan.titanchat.channel.setting;

import java.util.Arrays;

import org.bukkit.command.CommandSender;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
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
	
	/**
	 * Set Command - Sets the channel settings
	 */
	@Command(channel = true)
	@Description("Channel settings (\"/titanchat set help\" for more info)")
	@Usage("set [setting] <arguments>")
	public void set(CommandSender sender, Channel channel, String[] args) {
		if (args.length < 1) {
			plugin.getServer().dispatchCommand(sender, "titanchat @" + channel.getName() + " set help");
			return;
		}
		
		if (!channel.changeSetting(sender, args[0], Arrays.copyOfRange(args, 1, args.length)))
			plugin.send(MessageLevel.WARNING, sender, "Channel " + channel.getName() + " does not support such setting");
	}
}