package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat.MessageLevel;
import com.titankingdoms.nodinchan.titanchat.addon.Addon;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.channel.custom.CustomChannel;
import com.titankingdoms.nodinchan.titanchat.channel.util.Participant;
import com.titankingdoms.nodinchan.titanchat.command.CommandBase;
import com.titankingdoms.nodinchan.titanchat.command.CommandManager;
import com.titankingdoms.nodinchan.titanchat.command.Executor;
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
 * InformationCommand - Gives information on Commands, Channels, etc.
 * 
 * @author NodinChan
 *
 */
public class InformationCommand extends CommandBase {

	private ChannelManager cm;
	
	public InformationCommand() {
		this.cm = plugin.getManager().getChannelManager();
	}
	
	/**
	 * Addons Command - Lists out all addons
	 */
	@Command(server = true)
	@Description("Lists out all addons")
	@Usage("addons")
	public void addons(CommandSender sender, String[] args) {
		StringBuilder addons = new StringBuilder();
		
		for (Addon addon : plugin.getManager().getAddonManager().getAddons()) {
			if (addons.length() > 0)
				addons.append(", ");
			
			addons.append(addon.getName());
		}
		
		String[] addonLines = plugin.getFormatHandler().regroup("Addons: %message", addons.toString());
		
		StringBuilder types = new StringBuilder();
		
		for (Channel type : cm.getTypes()) {
			if (type.getType().equals("Standard"))
				continue;
			
			if (types.length() > 0)
				types.append(", ");
			
			types.append(type.getType());
		}
		
		String[] typeLines = plugin.getFormatHandler().regroup("Custom Types: %message", types.toString());
		
		StringBuilder channels = new StringBuilder();
		
		for (Channel channel : cm.getChannels()) {
			if (!(channel instanceof CustomChannel))
				continue;
			
			if (channels.length() > 0)
				channels.append(", ");
			
			channels.append(channel.getName());
		}
		
		String[] channelLines = plugin.getFormatHandler().regroup("Custom Channels: %message", channels.toString());
		
		sender.sendMessage("Addons: " + addonLines[0]);
		sender.sendMessage(Arrays.copyOfRange(addonLines, 1, addonLines.length));
		sender.sendMessage("Custom Types: " + typeLines[0]);
		sender.sendMessage(Arrays.copyOfRange(typeLines, 1, typeLines.length));
		sender.sendMessage("Custom Channels: " + channelLines[0]);
		sender.sendMessage(Arrays.copyOfRange(channelLines, 1, channelLines.length));
	}
	
	/**
	 * ColourCodes Command - Lists out available colour codes and respective colours
	 */
	@Command(server = true)
	@Aliases({ "colorcodes", "colours", "colors", "codes" })
	@Description("Lists out available colour codes and respective colours")
	@Usage("colourcodes <format>")
	public void colourcodes(CommandSender sender, String[] args) {
		try {
			if (args[0].equals("format")) {
				sender.sendMessage(ChatColor.AQUA + "=== Special Codes ===");
				sender.sendMessage(plugin.getFormatHandler().colourise("&kMagic") + ChatColor.WHITE + "(&k)");
				sender.sendMessage(plugin.getFormatHandler().colourise("&lBold") + ChatColor.WHITE + "(&l)");
				sender.sendMessage(plugin.getFormatHandler().colourise("&mStrike") + ChatColor.WHITE + "(&m)");
				sender.sendMessage(plugin.getFormatHandler().colourise("&nUnderline") + ChatColor.WHITE + "(&n)");
				sender.sendMessage(plugin.getFormatHandler().colourise("&oItalic") + ChatColor.WHITE + "(&o)");
				sender.sendMessage(plugin.getFormatHandler().colourise("&rReset") + ChatColor.WHITE + "(&r)");
				return;
			}
			
		} catch (IndexOutOfBoundsException e) {}
		
		sender.sendMessage(ChatColor.AQUA + "=== Colour Codes ===");
		sender.sendMessage(plugin.getFormatHandler().colourise("&0") + "&0");
		sender.sendMessage(plugin.getFormatHandler().colourise("&1") + "&1");
		sender.sendMessage(plugin.getFormatHandler().colourise("&2") + "&2");
		sender.sendMessage(plugin.getFormatHandler().colourise("&3") + "&3");
		sender.sendMessage(plugin.getFormatHandler().colourise("&4") + "&4");
		sender.sendMessage(plugin.getFormatHandler().colourise("&5") + "&5");
		sender.sendMessage(plugin.getFormatHandler().colourise("&6") + "&6");
		sender.sendMessage(plugin.getFormatHandler().colourise("&7") + "&7");
		sender.sendMessage(plugin.getFormatHandler().colourise("&8") + "&8");
		sender.sendMessage(plugin.getFormatHandler().colourise("&9") + "&9");
		sender.sendMessage(plugin.getFormatHandler().colourise("&a") + "&a");
		sender.sendMessage(plugin.getFormatHandler().colourise("&b") + "&b");
		sender.sendMessage(plugin.getFormatHandler().colourise("&c") + "&c");
		sender.sendMessage(plugin.getFormatHandler().colourise("&d") + "&d");
		sender.sendMessage(plugin.getFormatHandler().colourise("&e") + "&e");
		sender.sendMessage(plugin.getFormatHandler().colourise("&f") + "&f");
	}
	
	/**
	 * Help Command - Shows the command list
	 */
	@Command(server = true)
	@Aliases({ "?", "commands", "cmds" })
	@Description("Shows the command list")
	@Usage("help <page/command>")
	public void help(CommandSender sender, String[] args) {
		CommandManager cm = plugin.getManager().getCommandManager();
		
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
				sender.sendMessage(ChatColor.AQUA + "=== TitanChat Command List (" + (page + 1) + "/" + numPages + ") ===");
				
				for (int cmdNum = start; cmdNum < end; cmdNum++) {
					Executor executor = plugin.getManager().getCommandManager().getCommandExecutor(cmdNum);
					sender.sendMessage(ChatColor.AQUA + executor.getName() + " - " + executor.getDescription());
				}
				
				plugin.send(MessageLevel.INFO, sender, "Arguments: [NECESSARY] <OPTIONAL>");
				plugin.send(MessageLevel.INFO, sender, "\"/titanchat commands [command]\" for more info");
				
			} else {
				sender.sendMessage(ChatColor.AQUA + "=== TitanChat Command List ===");
				sender.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
				sender.sendMessage(ChatColor.AQUA + "Alias: /tc command [arguments]");
				plugin.send(MessageLevel.INFO, sender, "\"/titanchat commands [page]\" for command list");
			}
			
		} catch (IndexOutOfBoundsException e) {
			plugin.getServer().dispatchCommand(sender, "titanchat commands 1");
		} catch (NumberFormatException e) {
			Executor executor = cm.getCommandExecutor(args[0]);
			
			if (executor == null) {
				plugin.send(MessageLevel.WARNING, sender, "No info on command");
				return;
			}
			
			sender.sendMessage(ChatColor.AQUA + "=== " + executor.getName().toUpperCase().toCharArray()[0] + executor.getName().toLowerCase().substring(1) + " Command ===");
			sender.sendMessage(ChatColor.AQUA + "Description: " + executor.getDescription());
			
			StringBuilder str = new StringBuilder();
			
			for (String alias : executor.getAliases()) {
				if (str.length() > 0)
					str.append(", ");
				
				str.append(alias);
			}
			
			sender.sendMessage(ChatColor.AQUA + "Aliases: " + str.toString());
			sender.sendMessage(ChatColor.AQUA + "Usage: /titanchat " + executor.getUsage());
		}
	}
	
	/**
	 * Info Command - Gets the participants and followers of the channel
	 */
	@Command(server = true)
	@Aliases("i")
	@Description("Gets the participants and followers of the channel")
	@Usage("info <channel>")
	public void info(CommandSender sender, String[] args) {
		Channel channel = null;
		
		try {
			if (cm.existsByAlias(args[0]))
				channel = cm.getChannelByAlias(args[0]);
			else
				plugin.send(MessageLevel.WARNING, sender, "No such channel");
			
		} catch (IndexOutOfBoundsException e) {
			if (!(sender instanceof Player)) {
				plugin.send(MessageLevel.WARNING, sender, "Please specify a channel");
				usage(sender, "info");
				return;
			}
			
			channel = cm.getChannel((Player) sender);
			
			if (channel == null) {
				plugin.send(MessageLevel.WARNING, sender, "Specify a channel or join a channel to use this command");
				usage(sender, "info");
			}
		}
		
		if (channel == null)
			return;
		
		if (sender instanceof Player && !channel.access((Player) sender)) {
			channel.deny((Player) sender, null);
			return;
		}
		
		sender.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");
		
		List<String> participants = new ArrayList<String>();
		
		for (Participant participant : channel.getParticipants()) {
			if (participant.getPlayer() != null)
				participants.add(ChatColor.GREEN + participant.getName());
			else
				participants.add(ChatColor.RED + participant.getName());
		}
		
		String[] participantLines = plugin.getFormatHandler().regroup("Participants: %message", plugin.createList(participants));
		
		sender.sendMessage("Participants: " + participantLines[0]);
		sender.sendMessage(Arrays.copyOfRange(participantLines, 1, participantLines.length));
	}
	
	/**
	 * List Command - Lists all channels you have access to
	 */
	@Command
	@Description("Lists all channels you have access to")
	@Usage("list")
	public void list(Player player, String[] args) {
		List<String> accessList = new ArrayList<String>();
		
		for (Channel channel : cm.getChannels()) {
			if (channel.access(player))
				accessList.add(channel.getName());
		}
		
		player.sendMessage(ChatColor.AQUA + "Channels: " + plugin.createList(accessList));
	}
}