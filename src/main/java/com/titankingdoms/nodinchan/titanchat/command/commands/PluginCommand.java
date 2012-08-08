package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.nio.channels.Channel;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.command.CommandBase;
import com.titankingdoms.nodinchan.titanchat.command.info.*;
import com.titankingdoms.nodinchan.titanchat.util.Debugger;

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
 * PluginCommand - Plugin maintenance commands
 * 
 * @author NodinChan
 *
 */
public class PluginCommand extends CommandBase {
	
	/**
	 * Debug Command - Toggles the debug
	 */
	@Command(server = true)
	@Description("Toggles the debug")
	@Usage("debug [enable/disable] <type>")
	public void debug(CommandSender sender, Channel channel, String[] args) {
		if (sender instanceof Player && plugin.isStaff((Player) sender)) {
			plugin.send(MessageLevel.WARNING, sender, "You do not have permission");
			return;
		}
		
		if (args.length < 1) { invalidArgLength(sender, "debug"); return; }
		
		try {
			if (args[0].equalsIgnoreCase("enable")) {
				if (args[1].startsWith("none"))
					return;
				
				if (args[1].contains(","))
					for (String debugId : args[1].split(","))
						Debugger.enable(debugId);
				
			} else {
				if (args[1].startsWith("none"))
					return;
				
				if (args[1].contains(","))
					for (String debugId : args[1].split(","))
						Debugger.disable(debugId);
			}
			
		} catch (IndexOutOfBoundsException e) {
			if (args[0].equalsIgnoreCase("enable"))
				Debugger.enable("all");
			else
				Debugger.disable("all");
		}
	}
	
	/**
	 * Reload Command - Reloads the config
	 */
	@Command(server = true)
	@Description("Reloads the config")
	@Usage("reload")
	public void reload(CommandSender sender, Channel channel, String[] args) {
		if (sender instanceof Player && plugin.isStaff((Player) sender)) {
			plugin.send(MessageLevel.WARNING, sender, "You do not have permission");
			return;
		}
		
		if (sender instanceof Player)
			plugin.log(Level.INFO, "Reloading TitanChat...");
		
		plugin.send(MessageLevel.INFO, sender, "Reloading TitanChat...");
		plugin.reloadConfig();
		plugin.getVariableManager().unload();
		plugin.getFormatHandler().load();
		plugin.getManager().reload();
		plugin.send(MessageLevel.INFO, sender, "TitanChat reloaded");
		
		if (sender instanceof Player)
			plugin.log(Level.INFO, "TitanChat reloaded");
	}
}