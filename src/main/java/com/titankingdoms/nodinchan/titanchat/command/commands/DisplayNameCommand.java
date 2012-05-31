package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;
import com.titankingdoms.nodinchan.titanchat.util.displayname.DisplayName;
import com.titankingdoms.nodinchan.titanchat.util.displayname.DisplayNameChanger;

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
 * DisplayNameCommand - Manages Players' display names
 * 
 * @author NodinChan
 *
 */
public class DisplayNameCommand extends Command {
	
	private final DisplayNameChanger dnc;
	
	public DisplayNameCommand() {
		this.dnc = plugin.getDisplayNameChanger();
	}
	
	/**
	 * Name Command - Checks the real name of you or the Player
	 */
	@CommandID(name = "Name", triggers = "name")
	@CommandInfo(description = "Checks the real name of you or the Player", usage = "name <player>")
	public void name(Player player, String[] args) {
		try {
			DisplayName display = plugin.getDatabase().find(DisplayName.class).where().ieq("displayname", args[0]).findUnique();
			
			if (display == null) {
				plugin.sendWarning(player, "No such Player with the display name of " + args[0]);
				return;
			}
			
			plugin.sendInfo(player, "The real username of " + args[0] + " is " + display.getName());
			
		} catch (IndexOutOfBoundsException e) {
			plugin.sendInfo(player, "Your real username is " + player.getName());
		}
	}
	
	/**
	 * Nick Command - Changes your or your target's display name
	 */
	@CommandID(name = "Nick", triggers = "nick")
	@CommandInfo(description = "Sets your or your target's display name", usage = "nick [displayname] <player>")
	public void nick(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Nick"); return; }
		
		String displayname = "";
		
		for (String arg : args) {
			if (arg.startsWith("\"") && arg.endsWith("\"")) {
				displayname += arg.substring(1, arg.length() - 1);
				continue;
			}
			
			if (arg.startsWith("\"")) {
				displayname += arg + " ";
				continue;
			}
			
			if (displayname.length() < 1)
				continue;
			
			if (arg.endsWith("\"")) {
				displayname += arg;
				break;
			}
			
			displayname += arg + " ";
		}
		
		try {
			if (displayname.equals("")) {
				Player targetPlayer = plugin.getPlayer(args[1]);
				
				if (targetPlayer != null) {
					plugin.sendInfo(player, targetPlayer.getDisplayName() + " is now known as " + args[0]);
					dnc.set(targetPlayer, args[0]);
					plugin.sendInfo(targetPlayer, player.getDisplayName() + " changed your display name to " + args[0]);
					dnc.save(targetPlayer);
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else if (!displayname.endsWith("\"")) {
				plugin.sendWarning(player, "");
				
			} else {
				if (args[args.length - 1].endsWith("\""))
					throw new IndexOutOfBoundsException();
				
				Player targetPlayer = plugin.getPlayer(args[args.length - 1]);
				
				if (!displayname.endsWith("\"")) {
					plugin.sendInfo(player, targetPlayer.getDisplayName() + " is now known as " + targetPlayer.getDisplayName());
					return;
				}
				
				if (targetPlayer != null) {
					plugin.sendInfo(player, targetPlayer.getDisplayName() + " is now known as " + displayname.substring(1, displayname.length() - 1));
					dnc.set(targetPlayer, displayname.substring(1, displayname.length() - 1));
					plugin.sendInfo(targetPlayer, player.getDisplayName() + " changed your display name to " + displayname.substring(1, displayname.length() - 1));
					dnc.save(targetPlayer);
					
				} else { plugin.sendWarning(player, "Player not online"); }
			}
			
		} catch (IndexOutOfBoundsException e) {
			if (displayname.equals("")) {
				dnc.set(player, args[0]);
				dnc.save(player);
				plugin.sendInfo(player, "You are now known as " + args[0]);
				
			} else {
				if (!displayname.endsWith("\"")) {
					plugin.sendInfo(player, "You are now known as " + player.getDisplayName());
					return;
				}
				
				dnc.set(player, displayname.substring(1, displayname.length() - 1));
				dnc.save(player);
				plugin.sendInfo(player, "You are now known as " + displayname.substring(1, displayname.length() - 1));
			}
		}
	}
	
	/**
	 * Reset Command - Resets your or your target's display name
	 */
	@CommandID(name = "Reset", triggers = "reset")
	@CommandInfo(description = "Resets your or your target's display name", usage = "reset <player>")
	public void reset(Player player, String[] args) {
		try {
			Player targetPlayer = plugin.getPlayer(args[1]);
			
			if (targetPlayer != null) {
				plugin.sendInfo(player, targetPlayer.getDisplayName() + " is now known as " + targetPlayer.getName());
				dnc.set(targetPlayer, targetPlayer.getName());
				dnc.save(targetPlayer);
				plugin.sendInfo(targetPlayer, "You are now known as " + targetPlayer.getName());
				
			} else { plugin.sendWarning(player, "Player not online"); }
			
		} catch (IndexOutOfBoundsException e) {
			dnc.set(player, player.getName());
			dnc.save(player);
			plugin.sendInfo(player, "You are now known as " + player.getName());
		}
	}
}