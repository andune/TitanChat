package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.nio.channels.Channel;

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
	@Permission("TitanChat.nick.check")
	@Usage("name <player>")
	public void name(CommandSender sender, Channel channel, String[] args) {
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
	}
	
	/**
	 * Nick Command - Changes your or your target's display name
	 */
	@Command(server = true)
	@Description("Sets your or your target's display name")
	@Permission("TitanChat.nick.change")
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
			Player targetPlayer = plugin.getPlayer(targetName);
			
			if (targetPlayer != null) {
				plugin.send(MessageLevel.INFO, sender, targetPlayer.getDisplayName() + " is now known as " + displayname);
				dnc.set(targetPlayer, displayname);
				
				if (sender instanceof Player)
					plugin.send(MessageLevel.INFO, targetPlayer, ((Player) sender).getDisplayName() + " changed your display name to " + displayname);
				else
					plugin.send(MessageLevel.INFO, targetPlayer, sender.getName() + " changed your display name to " + displayname);
				
				dnc.save(targetPlayer);
				
			} else { plugin.send(MessageLevel.WARNING, sender, "Player not online"); }
			
		} else {
			if (sender instanceof Player) {
				plugin.send(MessageLevel.INFO, sender, "You are now known as " + displayname);
				dnc.set((Player) sender, displayname);
				dnc.save((Player) sender);
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You cannot change your display name"); }
		}
	}
	
	/**
	 * Reset Command - Resets your or your target's display name
	 */
	@Command(server = true)
	@Description("Resets your or your target's display name")
	@Permission("TitanChat.nick.reset")
	@Usage("reset <player>")
	public void reset(CommandSender sender, Channel channel, String[] args) {
		try {
			Player targetPlayer = plugin.getPlayer(args[0]);
			
			if (targetPlayer != null) {
				plugin.send(MessageLevel.INFO, sender, targetPlayer.getDisplayName() + " is now known as " + targetPlayer.getName());
				dnc.set(targetPlayer, targetPlayer.getName());
				dnc.save(targetPlayer);
				plugin.send(MessageLevel.INFO, targetPlayer, "You are now known as " + targetPlayer.getName());
				
			} else { plugin.send(MessageLevel.WARNING, sender, "Player not online"); }
			
		} catch (IndexOutOfBoundsException e) {
			if (sender instanceof Player) {
				plugin.send(MessageLevel.INFO, sender, "You are now known as " + sender.getName());
				dnc.set((Player) sender, sender.getName());
				dnc.save((Player) sender);
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You cannot reset your display name"); }
		}
	}
}