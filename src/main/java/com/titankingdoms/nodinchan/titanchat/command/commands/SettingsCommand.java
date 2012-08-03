package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.Channel.Range;
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
public final class SettingsCommand extends CommandBase {
	
	private void help(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "help", args))
			return;
		
		sender.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");
		sender.sendMessage(ChatColor.AQUA + "CHAT-COLOUR [COLOUR] - Sets the chat display colour of the channel");
		sender.sendMessage(ChatColor.AQUA + "COLOURING <TRUE/FALSE> - Sets whether the channel colours chat");
		sender.sendMessage(ChatColor.AQUA + "FORMAT [FORMAT] - Sets the format of the channel");
		sender.sendMessage(ChatColor.AQUA + "HELP - Shows the help menu");
		sender.sendMessage(ChatColor.AQUA + "NAME_COLOUR [COLOUR] - Sets the name display colour of the channel");
		sender.sendMessage(ChatColor.AQUA + "RADIUS [RADIUS] - Sets the radius of the channel");
		sender.sendMessage(ChatColor.AQUA + "RANGE [RANGE] - Sets the range of the channel");
		sender.sendMessage(ChatColor.AQUA + "TAG [TAG] - Sets the tag of the channel");
		sender.sendMessage(ChatColor.AQUA + "TOPIC [TOPIC] - Sets the topic of the channel");
		sender.sendMessage(ChatColor.AQUA + "WHITELIST <TRUE/FALSE> - Sets whether the channel requires whitelist");
	}
	
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
		
		if (args[0].equalsIgnoreCase("chat-colour")) {
			setChatColour(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (args[0].equalsIgnoreCase("colouring")) {
			setColouring(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (args[0].equalsIgnoreCase("format")) {
			setFormat(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			help(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (args[0].equalsIgnoreCase("name-colour")) {
			setNameColour(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (args[0].equalsIgnoreCase("radius")) {
			setRadius(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (args[0].equalsIgnoreCase("range")) {
			setRange(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (args[0].equalsIgnoreCase("tag")) {
			setTag(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (args[0].equalsIgnoreCase("topic")) {
			setTopic(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (args[0].equalsIgnoreCase("whitelist")) {
			setWhitelistOnly(sender, channel, Arrays.copyOfRange(args, 1, args.length));
			return;
		}
		
		if (!channel.changeSetting(sender, args[0], Arrays.copyOfRange(args, 1, args.length)))
			plugin.send(MessageLevel.WARNING, sender, "Channel " + channel.getName() + " does not support such setting");
	}
	
	private void setChatColour(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "chat-colour", args))
			return;
		
		try {
			if (channel.getAdmins().contains(sender.getName()) || !(sender instanceof Player) || plugin.isStaff((Player) sender)) {
				channel.getInfo().setChatColour(args[0]);
				plugin.send(MessageLevel.INFO, sender, "You have set the chat colour to " + channel.getInfo().getChatColour());
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You do not have permission"); }
			
		} catch (IndexOutOfBoundsException e) {
			plugin.send(MessageLevel.WARNING, sender, "Invalid Argument Length");
			plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat <@><channel> set chat-colour [colour]");
		}
	}
	
	private void setColouring(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "colouring", args))
			return;
		
		if (!(sender instanceof Player) || (channel.getAdmins().contains(sender.getName()) && hasPermission(sender, "TitanChat.colouring"))) {
			try {
				channel.getInfo().setColouring(Boolean.parseBoolean(args[0]));
				
			} catch (IndexOutOfBoundsException e) {
				channel.getInfo().setColouring(!channel.getInfo().colouring());
			}
			
			plugin.send(MessageLevel.INFO, sender, "You have set colouring to " + channel.getInfo().colouring());
			
		} else { plugin.send(MessageLevel.WARNING, sender, "You do not have permission"); }
	}
	
	private void setFormat(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "format", args))
			return;
		
		if (args.length > 0) {
			if (!(sender instanceof Player) || plugin.isStaff((Player) sender)) {
				StringBuilder str = new StringBuilder();
				
				for (String arg : args) {
					if (str.length() > 0)
						str.append(" ");
					
					str.append(arg);
				}
				
				channel.getInfo().setFormat(str.toString());
				plugin.send(MessageLevel.INFO, sender, "You have changed the format: " + channel.getInfo().getFormat());
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You do not have permission"); }
			
		} else {
			plugin.send(MessageLevel.WARNING, sender, "Invalid Argument Length");
			plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat <@><channel> set format [format]");
		}
	}
	
	private void setNameColour(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "name-colour", args))
			return;
		
		try {
			if (channel.getAdmins().contains(sender.getName()) || !(sender instanceof Player) || plugin.isStaff((Player) sender)) {
				channel.getInfo().setChatColour(args[0]);
				plugin.send(MessageLevel.INFO, sender, "You have set the name colour to " + channel.getInfo().getNameColour());
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You do not have permission"); }
			
		} catch (IndexOutOfBoundsException e) {
			plugin.send(MessageLevel.WARNING, sender, "Invalid Argument Length");
			plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat <@><channel> set name-colour [colour]");
		}
	}
	
	private void setRadius(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "radius", args))
			return;
		
		try {
			if (channel.getAdmins().contains(sender.getName()) || !(sender instanceof Player) || plugin.isStaff((Player) sender)) {
				channel.getInfo().setRadius(Integer.parseInt(args[0]));
				plugin.send(MessageLevel.INFO, sender, "You have set the radius to " + channel.getInfo().radius());
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You do not have permission"); }
			
		} catch (IndexOutOfBoundsException e) {
			plugin.send(MessageLevel.WARNING, sender, "Invalid Argument Length");
			plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat <@><channel> set radius [radius]");
			
		} catch (NumberFormatException e) {
			plugin.send(MessageLevel.WARNING, sender, "Invalid Integer");
			plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat <@><channel> set radius [radius]");
		}
	}
	
	private void setRange(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "range", args))
			return;
		
		try {
			if (!(sender instanceof Player) || plugin.isStaff((Player) sender)) {
				Range range = Range.fromName(args[0]);
				
				if (range != null) {
					channel.getInfo().setRange(range);
					plugin.send(MessageLevel.INFO, sender, "You have set the range to " + channel.getInfo().range().getName());
					
				} else { plugin.send(MessageLevel.WARNING, sender, "Invalid Range (channel, global, local, world)"); }
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You do not have permission"); }
			
		} catch (IndexOutOfBoundsException e) {
			plugin.send(MessageLevel.WARNING, sender, "Invalid Argument Length");
			plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat <@><channel> set range [range]");
		}
	}
	
	private void setTag(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "tag", args))
			return;
		
		try {
			if (channel.getAdmins().contains(sender.getName()) || !(sender instanceof Player) || plugin.isStaff((Player) sender)) {
				channel.getInfo().setTag(args[0]);
				plugin.send(MessageLevel.INFO, sender, "You have set the tag to " + channel.getInfo().getTag());
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You do not have permission"); }
			
		} catch (IndexOutOfBoundsException e) {
			plugin.send(MessageLevel.WARNING, sender, "Invalid Argument Length");
			plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat <@><channel> set tag [tag]");
		}
	}
	
	private void setTopic(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "topic", args))
			return;
		
		if (args.length > 0) {
			if (channel.getAdmins().contains(sender.getName()) || !(sender instanceof Player) || plugin.isStaff((Player) sender)) {
				channel.getInfo().setTag(args[0]);
				
				StringBuilder str = new StringBuilder();
				
				for (String arg : args) {
					if (str.length() > 0)
						str.append(" ");
					
					str.append(arg);
				}
				
				channel.getInfo().setTopic(str.toString());
				
				if (!channel.isParticipating(sender.getName()))
					plugin.send(MessageLevel.INFO, sender, "You have changed the topic: " + channel.getInfo().getTopic());
				
				plugin.send(MessageLevel.INFO, channel, ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName()) + " changed the topic: " + channel.getInfo().getTopic());
				
			} else { plugin.send(MessageLevel.WARNING, sender, "You do not have permission"); }
			
		} else {
			plugin.send(MessageLevel.WARNING, sender, "Invalid Argument Length");
			plugin.send(MessageLevel.INFO, sender, "Usage: /titanchat <@><channel> set topic [topic]");
		}
	}
	
	private void setWhitelistOnly(CommandSender sender, Channel channel, String[] args) {
		if (channel.changeSetting(sender, "whitelist", args))
			return;
		
		if (channel.getAdmins().contains(sender.getName()) || !(sender instanceof Player) || plugin.isStaff((Player) sender)) {
			try {
				channel.getInfo().setWhitelistOnly(Boolean.parseBoolean(args[0]));
				
			} catch (IndexOutOfBoundsException e) {
				channel.getInfo().setWhitelistOnly(!channel.getInfo().whitelistOnly());
			}
			
			plugin.send(MessageLevel.INFO, sender, "You have set whitelist-only to " + channel.getInfo().whitelistOnly());
			
		} else { plugin.send(MessageLevel.WARNING, sender, "You do not have permission"); }
	}
}