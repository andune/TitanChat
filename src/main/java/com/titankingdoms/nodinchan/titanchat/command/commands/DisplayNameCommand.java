package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.nio.channels.Channel;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.command.CommandBase;
import com.titankingdoms.nodinchan.titanchat.command.info.*;
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
public class DisplayNameCommand extends CommandBase {
	
	private final DisplayNameChanger dnc;
	
	public DisplayNameCommand() {
		this.dnc = plugin.getDisplayNameChanger();
	}
	
	/**
	 * Name Command - Checks the real username of you or of the Player
	 */
	@Command(server = true)
	@Description("Checks the real username of you or of the player")
	@Usage("name <player>")
	public void name(CommandSender sender, Channel channel, String[] args) {
		if (hasPermission(sender, "TitanChat.nick.check")) {
			try {
				if (!(sender instanceof Player)) {
					DisplayName display = plugin.getDatabase().find(DisplayName.class).where().ieq("displayname", args[0]).findUnique();
					
					if (display != null)
						plugin.send(MessageLevel.INFO, sender, "The real username of " + args[0] + " is " + display.getName());
					else
						plugin.send(MessageLevel.WARNING, sender, "No such player with the display name of " + args[0]);
				}
				
			} catch (IndexOutOfBoundsException e) {
				if (sender instanceof Player)
					plugin.send(MessageLevel.INFO, sender, "Your real username is " + sender.getName());
				else
					plugin.send(MessageLevel.INFO, sender, "You do not have a username");
			}
			
		} else { plugin.send(WARNING, sender, "You do not have permission"); }
	}
	
	/**
	 * Nick Command - Changes your or your target's display name
	 */
	@Command(server = true)
	@Description("Sets your or your target's display name")
	@Usage("nick [displayname] <player>")
	public void nick(CommandSender sender, Channel channel, String[] args) {
		if (args.length < 1) { invalidArgLength(sender, "nick"); return; }
		
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
		
		if (!targetName.isEmpty()) {
			if (hasPermission(sender, "TitanChat.nick.change.other")) {
				OfflinePlayer targetPlayer = plugin.getOfflinePlayer(targetName);
				
				if (targetPlayer.isOnline()) {
					plugin.send(MessageLevel.INFO, sender, targetPlayer.getPlayer().getDisplayName() + " is now known as " + displayname);
					dnc.set(targetPlayer.getPlayer(), displayname);
					
					if (sender instanceof Player)
						plugin.send(MessageLevel.INFO, targetPlayer.getPlayer(), ((Player) sender).getDisplayName() + " changed your display name to " + displayname);
					else
						plugin.send(MessageLevel.INFO, targetPlayer.getPlayer(), sender.getName() + " changed your display name to " + displayname);
					
					dnc.save(targetPlayer.getPlayer());
					
				} else { plugin.send(MessageLevel.WARNING, sender, getDisplayName(targetPlayer) + " is offline"); }
				
			} else { plugin.send(WARNING, sender, "You do not have permission"); }
			
		} else {
			if (hasPermission(sender, "TitanChat.nick.change")) {
				if (sender instanceof Player) {
					plugin.send(MessageLevel.INFO, sender, "You are now known as " + displayname);
					dnc.set((Player) sender, displayname);
					dnc.save((Player) sender);
					
				} else { plugin.send(MessageLevel.WARNING, sender, "You cannot change your display name"); }
				
			} else { plugin.send(WARNING, sender, "You do not have permission"); }
		}
	}
	
	/**
	 * Reset Command - Resets your or your target's display name
	 */
	@Command(server = true)
	@Description("Resets your or your target's display name")
	@Usage("reset <player>")
	public void reset(CommandSender sender, Channel channel, String[] args) {
		try {
			if (hasPermission(sender, "TitanChat.nick.reset.other")) {
				OfflinePlayer targetPlayer = plugin.getOfflinePlayer(args[0]);
				
				if (targetPlayer.isOnline()) {
					plugin.send(MessageLevel.INFO, sender, targetPlayer.getPlayer().getDisplayName() + " is now known as " + targetPlayer.getName());
					dnc.set(targetPlayer.getPlayer(), targetPlayer.getName());
					dnc.save(targetPlayer.getPlayer());
					plugin.send(MessageLevel.INFO, targetPlayer.getPlayer(), "You are now known as " + targetPlayer.getName());
					
				} else { plugin.send(MessageLevel.WARNING, sender, getDisplayName(targetPlayer) + " is offline"); }
				
			} else { plugin.send(WARNING, sender, "You do not have permission"); }
			
		} catch (IndexOutOfBoundsException e) {
			if (sender instanceof Player) {
				if (hasPermission(sender, "TitanChat.nick.reset")) {
					plugin.send(MessageLevel.INFO, sender, "You are now known as " + sender.getName());
					dnc.set((Player) sender, sender.getName());
					dnc.save((Player) sender);
					
				} else { plugin.send(WARNING, sender, "You do not have permission"); }
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You cannot reset your display name"); }
		}
	}
}