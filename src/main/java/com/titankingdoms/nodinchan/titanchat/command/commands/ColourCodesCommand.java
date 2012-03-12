package com.titankingdoms.nodinchan.titanchat.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.titankingdoms.nodinchan.titanchat.TitanChat;
import com.titankingdoms.nodinchan.titanchat.channel.ChannelManager;
import com.titankingdoms.nodinchan.titanchat.enums.Commands;

public class ColourCodesCommand extends Command {
	
	public ColourCodesCommand(TitanChat plugin, ChannelManager cm) {
		super(Commands.COLOURCODES, plugin, cm);
	}
	
	@Override
	public void execute(Player player, String[] args) {
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
}