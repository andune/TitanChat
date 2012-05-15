package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;
import com.titankingdoms.nodinchan.titanchat.command.CommandManager;

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
 * InformationCommand - Gives information on Commands, Channels, etc.
 * 
 * @author NodinChan
 *
 */
public class InformationCommand extends Command {

	private ChannelManager cm;
	
	public InformationCommand() {
		this.cm = plugin.getChannelManager();
	}
	
	/**
	 * ColourCodes Command - Lists out avalable colour codes and respective colours
	 */
	@CommandID(name = "ColourCodes", triggers = { "colourcodes", "colorcodes", "colours", "colors", "codes" }, requireChannel = false)
	@CommandInfo(description = "Lists out avalable colour codes and respective colours", usage = "colourcodes")
	public void colourcodes(Player player, String[] args) {
		String black = plugin.getFormatHandler().colourise("&0") + "&0";
		String darkblue = plugin.getFormatHandler().colourise("&1") + "&1";
		String green = plugin.getFormatHandler().colourise("&2") + "&2";
		String darkaqua = plugin.getFormatHandler().colourise("&3") + "&3";
		String red = plugin.getFormatHandler().colourise("&4") + "&4";
		String purple = plugin.getFormatHandler().colourise("&5") + "&5";
		String gold = plugin.getFormatHandler().colourise("&6") + "&6";
		String silver = plugin.getFormatHandler().colourise("&7") + "&7";
		String grey = plugin.getFormatHandler().colourise("&8") + "&8";
		String blue = plugin.getFormatHandler().colourise("&9") + "&9";
		String lightgreen = plugin.getFormatHandler().colourise("&a") + "&a";
		String aqua = plugin.getFormatHandler().colourise("&b") + "&b";
		String lightred = plugin.getFormatHandler().colourise("&c") + "&c";
		String lightpurple = plugin.getFormatHandler().colourise("&d") + "&d";
		String yellow = plugin.getFormatHandler().colourise("&e") + "&e";
		String white = plugin.getFormatHandler().colourise("&f") + "&f";
		String magical = plugin.getFormatHandler().colourise("&kMagical");
		String bold = plugin.getFormatHandler().colourise("&lBold");
		String striked = plugin.getFormatHandler().colourise("&mStriked");
		String underlined = plugin.getFormatHandler().colourise("&nUnderlined");
		String italic = plugin.getFormatHandler().colourise("&oItalic");
		String comma = ChatColor.WHITE + ", ";
		
		player.sendMessage(ChatColor.AQUA + "=== Colour Codes ===");
		player.sendMessage(black + comma + darkblue + comma + green + comma + darkaqua + comma);
		player.sendMessage(red + comma + purple + comma + gold + comma + silver + comma);
		player.sendMessage(grey + comma + blue + comma + lightgreen + comma + aqua + comma);
		player.sendMessage(lightred + comma + lightpurple + comma + yellow + comma + white + comma);
		player.sendMessage("And also the Magical &k (" + magical + ChatColor.WHITE + ")");
		player.sendMessage(bold + ChatColor.WHITE + "(&l)");
		player.sendMessage(striked + ChatColor.WHITE + "(&m)");
		player.sendMessage(underlined + ChatColor.WHITE + "(&n)");
		player.sendMessage(italic + ChatColor.WHITE + "(&o)");
	}
	
	/**
	 * Commands Command - Shows the command list
	 */
	@CommandID(name = "Commands", triggers = { "commands", "cmds", "help", "?" }, requireChannel = false)
	@CommandInfo(description = "Shows the command list", usage = "commands <page/command>")
	public void commands(Player player, String[] args) {
		CommandManager cm = plugin.getCommandManager();
		
		try {
			int page = Integer.parseInt(args[0]) - 1;
			int numPages = cm.getCommandAmount() / 10;
			int start = page * 10;
			int end = start + 10;
			
			if (cm.getCommandAmount() % 10 != 0 && (numPages * 10) - cm.getCommandAmount() < 0)
				numPages++;
			
			if (end > cm.getCommandAmount())
				end = cm.getCommandAmount();
			
			if (page + 1 > 0 || page + 1 <= numPages) {
				player.sendMessage(ChatColor.AQUA + "=== TitanChat Command List (" + (page + 1) + "/" + numPages + ") ===");
				for (int cmdNum = start; cmdNum < end; cmdNum++)
					player.sendMessage(ChatColor.AQUA + plugin.getCommandManager().getCommandExecutor(cmdNum).getAnnotation(CommandID.class).name());
				plugin.sendInfo(player, "Arguments: [NECESSARY] <OPTIONAL>");
				plugin.sendInfo(player, "\"/titanchat commands [command]\" for more info");
				
			} else {
				player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
				player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
				player.sendMessage(ChatColor.AQUA + "Alias: /tc command [arguments]");
				plugin.sendInfo(player, "\"/titanchat commands [page]\" for command list");
			}
			
		} catch (IndexOutOfBoundsException e) {
			plugin.getServer().dispatchCommand(player, "titanchat commands 1");
		} catch (NumberFormatException e) {
			Method method = cm.getCommandExecutor(args[0]);
			
			if (method == null) {
				plugin.sendWarning(player, "No info on command");
				return;
			}
			
			player.sendMessage(ChatColor.AQUA + "=== " + method.getAnnotation(CommandID.class).name() + " Command ===");
			
			if (method.getAnnotation(CommandInfo.class) != null)
				player.sendMessage(ChatColor.AQUA + "Description: " + method.getAnnotation(CommandInfo.class).description());
			
			StringBuilder str = new StringBuilder();
			
			for (String alias : method.getAnnotation(CommandID.class).triggers()) {
				if (str.length() > 0)
					str.append(", ");
				
				str.append(alias);
			}
			
			player.sendMessage(ChatColor.AQUA + "Aliases: " + str.toString());
			
			if (method.getAnnotation(CommandInfo.class) != null)
				player.sendMessage(ChatColor.AQUA + "Usage: /titanchat " + method.getAnnotation(CommandInfo.class).usage());
		}
	}
	
	/**
	 * Info Command - Gets the participants and followers of the channel
	 */
	@CommandID(name = "Info", triggers = "info")
	@CommandInfo(description = "Gets the participants and followers of the channel", usage = "info <channel>")
	public void info(Player player, String[] args) {
		try {
			if (cm.exists(args[0])) {
				Channel channel = cm.getChannel(args[0]);
				
				if (channel.canAccess(player)) {
					player.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");

					List<String> offline = new ArrayList<String>();
					List<String> online = new ArrayList<String>();
					
					for (String participant : channel.getParticipants()) {
						if (plugin.getPlayer(participant) != null)
							online.add(participant);
						else
							offline.add(participant);
					}
					
					player.sendMessage(ChatColor.AQUA + "Online participants: " + plugin.createList(online));
					player.sendMessage(ChatColor.AQUA + "Offline participants: " + plugin.createList(offline));
					player.sendMessage(ChatColor.AQUA + "Followers: " + plugin.createList(cm.getFollowers(channel)));
				}
				
			} else { plugin.sendWarning(player, "No such channel"); }
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			if (channel == null) {
				plugin.sendWarning(player, "Specify a channel or join a channel to use this command");
				return;
			}
			
			player.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");
			player.sendMessage(ChatColor.AQUA + "Participants: " + plugin.createList(channel.getParticipants()));
			player.sendMessage(ChatColor.AQUA + "Followers: " + plugin.createList(channel.getFollowerList()));
		}
	}
	
	/**
	 * List Command - Lists all channels you have access to
	 */
	@CommandID(name = "List", triggers = "list")
	@CommandInfo(description = "Lists all channels you have access to", usage = "list")
	public void list(Player player, String[] args) {
		player.sendMessage(ChatColor.AQUA + "Channels: " + plugin.createList(cm.getAccessList(player)));
	}
}