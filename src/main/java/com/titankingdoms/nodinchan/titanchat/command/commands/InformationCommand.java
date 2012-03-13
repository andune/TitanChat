package com.titankingdoms.nodinchan.titanchat.command.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.Channel;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.command.Command;
import com.titankingdoms.nodinchan.titanchat.command.CommandID;
import com.titankingdoms.nodinchan.titanchat.command.CommandInfo;
import com.titankingdoms.nodinchan.titanchat.command.CommandManager;

public class InformationCommand extends Command {
	
	private ChannelManager cm;
	
	public InformationCommand(TitanChat plugin) {
		super(plugin);
		this.cm = plugin.getChannelManager();
	}
	
	@CommandID(name = "ColourCodes", triggers = { "colourcodes", "colorcodes", "colours", "colors", "codes" })
	@CommandInfo(description = "Lists out avalable colour codes and respective colours", usage = "colourcodes")
	public void colourcodes(Player player, String[] args) {
		String black = plugin.getFormat().colourise("&0") + "&0";
		String darkblue = plugin.getFormat().colourise("&1") + "&1";
		String green = plugin.getFormat().colourise("&2") + "&2";
		String darkaqua = plugin.getFormat().colourise("&3") + "&3";
		String red = plugin.getFormat().colourise("&4") + "&4";
		String purple = plugin.getFormat().colourise("&5") + "&5";
		String gold = plugin.getFormat().colourise("&6") + "&6";
		String silver = plugin.getFormat().colourise("&7") + "&7";
		String grey = plugin.getFormat().colourise("&8") + "&8";
		String blue = plugin.getFormat().colourise("&9") + "&9";
		String lightgreen = plugin.getFormat().colourise("&a") + "&a";
		String aqua = plugin.getFormat().colourise("&b") + "&b";
		String lightred = plugin.getFormat().colourise("&c") + "&c";
		String lightpurple = plugin.getFormat().colourise("&d") + "&d";
		String yellow = plugin.getFormat().colourise("&e") + "&e";
		String white = plugin.getFormat().colourise("&f") + "&f";
		String magical = plugin.getFormat().colourise("&kMagical");
		String comma = ChatColor.WHITE + ",";
		
		player.sendMessage(ChatColor.AQUA + "=== Colour Codes ===");
		player.sendMessage(black + comma + darkblue + comma + green + comma + darkaqua + comma);
		player.sendMessage(red + comma + purple + comma + gold + comma + silver + comma);
		player.sendMessage(grey + comma + blue + comma + lightgreen + comma + aqua + comma);
		player.sendMessage(lightred + comma + lightpurple + comma + yellow + comma + white + comma);
		player.sendMessage("And also the Magical &k (" + magical + ChatColor.WHITE + ")");
	}
	
	@CommandID(name = "Commands", triggers = { "commands" })
	@CommandInfo(description = "Shows the command list", usage = "commands <page/command>")
	public void commands(Player player, String[] args) {
		CommandManager cm = plugin.getCommandManager();
		
		try {
			int page = Integer.parseInt(args[0]);
			int numPages = cm.getCommandAmount() / 5;
			int start = page * 5;
			int end = start + 5;
			
			if (cm.getCommandAmount() % 5 != 0 && (numPages * 5) - cm.getCommandAmount() < 0)
				numPages++;
			
			if (end > cm.getCommandAmount())
				end = cm.getCommandAmount();
			
			if (page > 0 || page < numPages) {
				player.sendMessage(ChatColor.AQUA + "=== TitanChat Command List (" + page + "/" + numPages + ") ===");
				for (int cmdNum = start; cmdNum < end; cmdNum++) {
					player.sendMessage(ChatColor.AQUA + plugin.getCommandManager().getCommandExecutor(cmdNum).getName());
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
			if (cm.getCommandExecutor(args[0]) == null) {
				plugin.sendWarning(player, "No info on command");
				return;
			}
			
			player.sendMessage(ChatColor.AQUA + "=== " + cm.getCommandExecutor(args[0]).getName() + " Command ===");
			
			Method method = cm.getCommandExecutor(args[0]).getMethod();
			
			if (method.getAnnotation(CommandInfo.class) != null)
				player.sendMessage(ChatColor.AQUA + "Description: " + method.getAnnotation(CommandInfo.class).description());
			
			StringBuilder cmdStr = new StringBuilder();
			
			for (String alias : method.getAnnotation(CommandID.class).triggers()) {
				if (cmdStr.length() > 0)
					cmdStr.append(", ");
				
				cmdStr.append(alias);
			}
			
			player.sendMessage(ChatColor.AQUA + "Aliases: " + cmdStr.toString());
			
			if (method.getAnnotation(CommandInfo.class) != null)
				player.sendMessage(ChatColor.AQUA + "Usage: /titanchat " + method.getAnnotation(CommandInfo.class).usage());
		}
	}
	
	@CommandID(name = "Info", triggers = { "info" })
	@CommandInfo(description = "Gets the participants and followers of the channel", usage = "info <channel>")
	public void info(Player player, String[] args) {
		try {
			Channel channel = cm.getChannel(args[0]);
			
			if (channel != null) {
				if (channel.canAccess(player)) {
					player.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");
					player.sendMessage(ChatColor.AQUA + "Participants: " + plugin.createList(channel.getParticipants()));
					player.sendMessage(ChatColor.AQUA + "Followers: " + plugin.createList(channel.getFollowerList()));
				}
				
			} else {
				plugin.sendWarning(player, "No such channel");
			}
			
		} catch (IndexOutOfBoundsException e) {
			Channel channel = cm.getChannel(player);
			
			player.sendMessage(ChatColor.AQUA + "=== " + channel.getName() + " ===");
			player.sendMessage(ChatColor.AQUA + "Participants: " + plugin.createList(channel.getParticipants()));
			player.sendMessage(ChatColor.AQUA + "Followers: " + plugin.createList(channel.getFollowerList()));
		}
	}
	
	@CommandID(name = "List", triggers = { "list" })
	@CommandInfo(description = "Lists all channels you have access to", usage = "list")
	public void list(Player player, String[] args) {
		List<String> channels = new ArrayList<String>();
		
		for (Channel channel : cm.getChannels()) {
			if (channel.canAccess(player))
				channels.add(channel.getName());
		}
		
		player.sendMessage(ChatColor.AQUA + "Channels: " + plugin.createList(channels));
	}
}