package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.info.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.info.CommandInfo;
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
	@CommandID(name = "Name", aliases = "name")
	@CommandInfo(description = "Checks the real name of you or the Player", usage = "name <player>")
	public void name(Player player, String[] args) {
		if (plugin.getPermsBridge().has(player, "TitanChat.nick.check")) {
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
			
		} else { plugin.sendWarning(player, "You do not have permission"); }
	}
	
	/**
	 * Nick Command - Changes your or your target's display name
	 */
	@CommandID(name = "Nick", aliases = "nick")
	@CommandInfo(description = "Sets your or your target's display name", usage = "nick [displayname] <player>")
	public void nick(Player player, String[] args) {
		if (args.length < 1) { invalidArgLength(player, "Nick"); return; }
		
		if (plugin.getPermsBridge().has(player, "TitanChat.nick.change")) {
			StringBuilder displaynameStr = new StringBuilder();
			
			for (String arg : args) {
				if (displaynameStr.length() > 0)
					displaynameStr.append(" ");
				
				displaynameStr.append(arg);
			}
			
			int openQ = displaynameStr.toString().indexOf("\"");
			int closeQ = displaynameStr.toString().lastIndexOf("\"");
			
			String displayname = "";
			
			if (openQ == closeQ || openQ < 0 || closeQ < 0)
				displayname = args[0];
			else
				displayname = displaynameStr.toString().substring(openQ + 1, closeQ);
			
			String targetName = displaynameStr.toString().substring(closeQ + 1).trim().split(" ")[0];
			
			if (!targetName.equals("")) {
				Player targetPlayer = plugin.getPlayer(targetName);
				
				if (targetPlayer != null) {
					plugin.sendInfo(player, targetPlayer.getDisplayName() + " is now known as " + displayname);
					dnc.set(targetPlayer, displayname);
					plugin.sendInfo(targetPlayer, player.getDisplayName() + " changed your display name to " + displayname);
					dnc.save(targetPlayer);
					
				} else { plugin.sendWarning(player, "Player not online"); }
				
			} else {
				plugin.sendInfo(player, "You are now known as " + displayname);
				dnc.set(player, displayname);
				dnc.save(player);
			}
			
		} else { plugin.sendWarning(player, "You do not have permission"); }
	}
	
	/**
	 * Reset Command - Resets your or your target's display name
	 */
	@CommandID(name = "Reset", aliases = "reset")
	@CommandInfo(description = "Resets your or your target's display name", usage = "reset <player>")
	public void reset(Player player, String[] args) {
		if (plugin.getPermsBridge().has(player, "TitanChat.nick.reset")) {
			try {
				Player targetPlayer = plugin.getPlayer(args[0]);
				
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
			
		} else { plugin.sendWarning(player, "You do not have permission"); }
	}
}