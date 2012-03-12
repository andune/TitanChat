package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class CommandsCommand extends Command {
	
	public CommandsCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.COMMANDS, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
		try {
			int page = Integer.parseInt(args[0]);
			int numPages = Commands.values().length / 5;
			int start = page * 5;
			int end = start + 5;
			
			if (Commands.values().length % 5 != 0 && (numPages * 5) - Commands.values().length < 0)
				numPages++;
			
			if (end > Commands.values().length)
				end = Commands.values().length;
			
			if (page > 0 || page < numPages) {
				player.sendMessage(ChatColor.AQUA + "=== TitanChat Command List (" + page + "/" + numPages + ") ===");
				for (int cmdNum = start; cmdNum < end; cmdNum++) {
					player.sendMessage(ChatColor.AQUA + Commands.values()[cmdNum].toString().toLowerCase());
				}
				plugin.sendInfo(player, "Arguments: [NECESSARY] <OPTIONAL>");
				plugin.sendInfo(player, "'/titanchat commands [command]' for more info");
				
			} else {
				player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
				player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
				player.sendMessage(ChatColor.AQUA + "Alias: /tc command [arguments]");
				plugin.sendInfo(player, "'/titanchat commands [page]' for command list");
			}
			
		} catch (IndexOutOfBoundsException e) {
			player.sendMessage(ChatColor.AQUA + "TitanChat Commands");
			player.sendMessage(ChatColor.AQUA + "Command: /titanchat [command] [arguments]");
			player.sendMessage(ChatColor.AQUA + "Alias: /tc command [arguments]");
			plugin.sendInfo(player, "'/titanchat commands [page]' for command list");
			
		} catch (NumberFormatException e) {
			if (Commands.fromName(args[0]) == null) {
				plugin.sendWarning(player, "No info on command");
				return;
			}
			
			player.sendMessage(ChatColor.AQUA + "=== " + Commands.fromName(args[0]).getName() + " Command ===");
			player.sendMessage(ChatColor.AQUA + "Description: " + Commands.fromName(args[0]).getDescription());
			
			StringBuilder cmdStr = new StringBuilder();
			
			for (String alias : Commands.fromName(args[0]).getAliases()) {
				if (cmdStr.length() > 0)
					cmdStr.append(", ");
				
				cmdStr.append(alias);
			}
			
			player.sendMessage(ChatColor.AQUA + "Aliases: " + cmdStr.toString());
			player.sendMessage(ChatColor.AQUA + "Usage: " + Commands.fromName(args[0]).getUsage());
		}
	}
}